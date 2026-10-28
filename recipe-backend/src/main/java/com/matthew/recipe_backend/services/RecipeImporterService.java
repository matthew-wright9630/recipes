package com.matthew.recipe_backend.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matthew.recipe_backend.dtos.RecipeImportRequest;
import com.matthew.recipe_backend.dtos.RecipeImportResponse;
import com.matthew.recipe_backend.dtos.RecipeIngredientDto;
import com.matthew.recipe_backend.models.RecipeIngredient;
import com.matthew.recipe_backend.repositories.RecipeDirectionRepository;
import com.matthew.recipe_backend.repositories.RecipeRepository;

@Service
public class RecipeImporterService {

    private final RecipeRepository recipeRepository;
    private final UserService userService;
    private final RecipeIngredientService recipeIngredientService;
    private final ObjectMapper objectMapper;

    public RecipeImporterService(RecipeRepository recipeRepository, UserService userService,
            RecipeIngredientService recipeIngredientService, ObjectMapper objectMapper) {
        this.recipeRepository = recipeRepository;
        this.userService = userService;
        this.recipeIngredientService = recipeIngredientService;
        this.objectMapper = objectMapper;
    }

    public String fetchPage(String url) throws IOException {
        Document document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(10000)
                .get();

        String extractedDocument = extractJsonLd(document.html());

        return extractedDocument;
    }
    // public RecipeImportResponse importRecipe(String url) {

    // }
    public String extractJsonLd(String html) throws JsonProcessingException {
        Document document = Jsoup.parse(html);

        Elements scripts = document.select(
                "script[type=application/ld+json]");

        for (Element script : scripts) {
            String json = script.data();

            if (json.isBlank()) {
                continue;
            }

            JsonNode root = objectMapper.readTree(json);

            // Check whether the root object is the recipe.
            if (isRecipe(root)) {
                return root.toString();
            }

            // Otherwise, search the @graph array.
            JsonNode graph = root.path("@graph");

            if (graph.isArray()) {
                for (JsonNode node : graph) {
                    if (isRecipe(node)) {
                        return node.toString();
                    }
                }
            }
        }

        return null;
    }

    private boolean isRecipe(JsonNode node) {
        JsonNode type = node.path("@type");

        if (type.isTextual()) {
            return "Recipe".equals(type.asText());
        }

        if (type.isArray()) {
            for (JsonNode item : type) {
                if (item.isTextual()
                        && "Recipe".equals(item.asText())) {
                    return true;
                }
            }
        }

        return false;
    }

    public RecipeImportResponse mapRecipe(String recipeJson, String url)
            throws JsonProcessingException {

        JsonNode recipe = objectMapper.readTree(recipeJson);

        String name = recipe.path("name").asText(null);

        Integer prepTime = parseDuration(
                recipe.path("prepTime").asText(null));

        Integer cookTime = parseDuration(
                recipe.path("cookTime").asText(null));

        Integer servings = parseServings(recipe.path("recipeYield"));

        List<RecipeIngredientDto> recipeIngredients = parseRecipeIngredient(recipe.path("recipeIngredient"));

        return new RecipeImportResponse(
                name,
                recipeIngredients,
                servings,
                prepTime,
                cookTime,
                url);
    }

    private Integer parseDuration(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Math.toIntExact(Duration.parse(value).toMinutes());
        } catch (DateTimeParseException | ArithmeticException e) {
            return null;
        }
    }

    private List<RecipeIngredientDto> parseRecipeIngredient(JsonNode ingredients) {
        if (ingredients == null || !ingredients.isArray()) {
            return List.of();
        }

        List<RecipeIngredientDto> recipeIngredients = new ArrayList<>();

        for (int i = 0; i < ingredients.size(); i++) {
            String ingredient = ingredients.get(i).asText();

            ParsedQuantity quantityResult = extractQuantity(ingredient);

            ParsedUnit unitResult = extractUnit(quantityResult.remainingText());
            boolean alternativeMeasurement = quantityResult.quantity() != null
                    && unitResult.unit() != null
                    && hasAlternativeMeasurement(unitResult.remainingText());

            if (alternativeMeasurement) {
                quantityResult = new ParsedQuantity(null, ingredient);
                unitResult = new ParsedUnit(null, ingredient);
            }

            ParsedNotes parentheticalResult = extractParentheticalNotes(unitResult.remainingText());

            ParsedNotes commaResult = extractCommaNotes(parentheticalResult.name());

            String notes = parentheticalResult.notes();

            if (commaResult.notes() != null) {
                notes = notes == null
                        ? commaResult.notes()
                        : notes + ", " + commaResult.notes();
            }
            RecipeIngredientDto recipeIngredientDto = new RecipeIngredientDto(commaResult.name,
                    quantityResult.quantity(), unitResult.unit(), notes, i);
            recipeIngredients.add(recipeIngredientDto);
        }

        return recipeIngredients;
    }

    private static final Pattern QUANTITY_PATTERN = Pattern.compile(
            "^(\\d+\\s*-\\s*\\d+|\\d+\\s+\\d+/\\d+|\\d+/\\d+|\\d*\\.\\d+|\\d+|[¼½¾⅓⅔⅛⅜⅝⅞])\\s*");

    private BigDecimal parseQuantity(String value) {
        return switch (value) {
            case "¼" -> new BigDecimal("0.25");
            case "½" -> new BigDecimal("0.5");
            case "¾" -> new BigDecimal("0.75");
            case "⅓" -> new BigDecimal("0.333333");
            case "⅔" -> new BigDecimal("0.666667");
            case "⅛" -> new BigDecimal("0.125");
            case "⅜" -> new BigDecimal("0.375");
            case "⅝" -> new BigDecimal("0.625");
            case "⅞" -> new BigDecimal("0.875");
            default -> {
                if (value.contains(" ")) {
                    String[] parts = value.trim().split("\\s+");
                    yield new BigDecimal(parts[0]).add(parseFraction(parts[1]));
                } else if (value.contains("/")) {
                    yield parseFraction(value);
                } else {
                    yield new BigDecimal(value);
                }
            }
        };
    }

    private BigDecimal parseFraction(String value) {
        String[] parts = value.split("/");
        return new BigDecimal(parts[0])
                .divide(new BigDecimal(parts[1]), 6, RoundingMode.HALF_UP);
    }

    private record ParsedQuantity(BigDecimal quantity, String remainingText) {
    }

    private ParsedQuantity extractQuantity(String ingredient) {
        String trimmedIngredient = ingredient.trim();
        Matcher matcher = QUANTITY_PATTERN.matcher(trimmedIngredient);

        if (!matcher.find()) {
            return new ParsedQuantity(null, trimmedIngredient);
        }

        String quantityText = matcher.group(1);

        if (quantityText.contains("-")) {
            return new ParsedQuantity(null, trimmedIngredient);
        }

        String remainingText = trimmedIngredient
                .substring(matcher.end())
                .trim();

        try {
            return new ParsedQuantity(
                    parseQuantity(quantityText),
                    remainingText);
        } catch (NumberFormatException e) {
            return new ParsedQuantity(null, trimmedIngredient);
        }
    }

    private record ParsedUnit(String unit, String remainingText) {
    }

    private ParsedUnit extractUnit(String text) {
        String normalizedText = text.toLowerCase(Locale.ROOT).trim();

        String unit = COMMON_UNITS.stream()
                .sorted((a, b) -> Integer.compare(b.length(), a.length()))
                .filter(candidate -> normalizedText.startsWith(candidate.toLowerCase(Locale.ROOT)))
                .filter(candidate -> normalizedText.length() == candidate.length()
                        || Character.isWhitespace(normalizedText.charAt(candidate.length())))
                .findFirst()
                .orElse(null);

        if (unit == null) {
            return new ParsedUnit(null, text.trim());
        }

        String remainingText = text.trim()
                .substring(unit.length())
                .trim();

        return new ParsedUnit(unit, remainingText);
    }

    private boolean hasAlternativeMeasurement(String text) {
        String alternativePattern = "(?i)^\\s*(?:/|or\\b)\\s*"
                + "\\d+(?:\\.\\d+|\\s+\\d+/\\d+|/\\d+)?\\s*";

        Pattern pattern = Pattern.compile(alternativePattern);
        Matcher matcher = pattern.matcher(text);

        if (!matcher.find()) {
            return false;
        }

        String remaining = text.substring(matcher.end()).trim();

        return extractUnit(remaining).unit() != null;
    }

    private static final Set<String> COMMON_UNITS = Set.of(
            "tsp",
            "teaspoon",
            "teaspoons",
            "tbsp",
            "tablespoon",
            "tablespoons",
            "fl oz",
            "fluid ounce",
            "fluid ounces",
            "cup",
            "cups",
            "pt",
            "pint",
            "pints",
            "qt",
            "quart",
            "quarts",
            "gal",
            "gallon",
            "gallons",
            "ml",
            "l",
            "oz",
            "ounce",
            "ounces",
            "lb",
            "pound",
            "pounds",
            "g",
            "gram",
            "grams",
            "kg",
            "kilogram",
            "kilograms",
            "mg",
            "pinch",
            "pinches",
            "dash",
            "dashes",
            "can",
            "cans",
            "clove",
            "cloves",
            "head",
            "heads",
            "bunch",
            "bunches",
            "batch");

    private record ParsedNotes(String name, String notes) {
    }

    private ParsedNotes extractParentheticalNotes(String text) {
        List<String> notes = new ArrayList<>();
        StringBuilder name = new StringBuilder();
        StringBuilder note = new StringBuilder();

        int depth = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '(') {
                if (depth == 0) {
                    note.setLength(0);
                } else {
                    note.append(c);
                }
                depth++;
            } else if (c == ')') {
                depth--;

                if (depth == 0) {
                    String extractedNote = note.toString().trim();

                    // Remove a leading comma from entries like "(, minced)".
                    if (extractedNote.startsWith(",")) {
                        extractedNote = extractedNote.substring(1).trim();
                    }

                    if (!extractedNote.isEmpty()) {
                        notes.add(extractedNote);
                    }
                } else if (depth > 0) {
                    note.append(c);
                } else {
                    // Keep an unmatched closing parenthesis rather than losing it.
                    name.append(c);
                    depth = 0;
                }
            } else if (depth > 0) {
                note.append(c);
            } else {
                name.append(c);
            }
        }

        // Preserve any unmatched opening parenthesis and its contents.
        if (depth > 0) {
            name.append('(').append(note);
        }

        String ingredientName = name.toString()
                .replaceAll("\\s{2,}", " ")
                .trim();

        String ingredientNotes = notes.isEmpty()
                ? null
                : String.join(", ", notes);

        return new ParsedNotes(ingredientName, ingredientNotes);
    }

    private ParsedNotes extractCommaNotes(String text) {
        int commaIndex = text.indexOf(',');

        if (commaIndex == -1) {
            return new ParsedNotes(text.trim(), null);
        }

        String name = text.substring(0, commaIndex).trim();
        String notes = text.substring(commaIndex + 1).trim();

        return new ParsedNotes(name, notes.isEmpty() ? null : notes);
    }

    private Integer parseServings(JsonNode recipeYield) {
        if (recipeYield == null || recipeYield.isMissingNode()
                || recipeYield.isNull()) {
            return null;
        }

        if (recipeYield.isArray()) {
            for (JsonNode value : recipeYield) {
                Integer servings = parseServings(value);
                if (servings != null) {
                    return servings;
                }
            }
            return null;
        }

        String value = recipeYield.asText().trim();

        Matcher matcher = Pattern.compile("\\d+").matcher(value);

        if (matcher.find()) {
            int servings = Integer.parseInt(matcher.group());

            return servings > 0 ? servings : null;
        }

        return null;
    }
}
