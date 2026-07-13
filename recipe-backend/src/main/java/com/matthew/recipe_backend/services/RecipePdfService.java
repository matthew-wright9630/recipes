package com.matthew.recipe_backend.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import com.matthew.recipe_backend.dtos.RecipeDirectionsDto;
import com.matthew.recipe_backend.dtos.RecipeDto;
import com.matthew.recipe_backend.dtos.RecipeIngredientDto;

@Service
public class RecipePdfService {

    private final ImageService userImageService;

    public RecipePdfService(ImageService userImageService) {
        this.userImageService = userImageService;
    }

    public byte[] generateRecipePdf(RecipeDto recipe) throws IOException {
        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {

                byte[] imageBytes = userImageService.downloadFile(
                        recipe.imageUrl() + "-medium.jpg");

                PDImageXObject image = PDImageXObject.createFromByteArray(
                        document,
                        imageBytes,
                        "recipe-image");

                float y = 750;

                // Title
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 20);
                content.newLineAtOffset(50, y);
                content.showText(recipe.name());
                content.endText();

                // Image
                content.drawImage(image, 50, y - 300, 300, 225);

                y -= 350;

                // Ingredients header
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
                content.newLineAtOffset(50, y);
                content.showText("Ingredients:");
                content.endText();

                y -= 20;

                // Ingredients
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                content.newLineAtOffset(60, y);

                for (RecipeIngredientDto ingredient : recipe.recipeIngredients()) {

                    StringBuilder line = new StringBuilder("- ");

                    if (ingredient.quantity() != null) {
                        line.append(ingredient.quantity()).append(" ");
                    }

                    if (ingredient.unit() != null && !ingredient.unit().isBlank()) {
                        line.append(ingredient.unit()).append(" of ");
                    }

                    line.append(ingredient.name());

                    content.showText(line.toString());

                    content.newLineAtOffset(0, -20);
                    y -= 20;
                }

                content.endText();

                y -= 20;

                // Directions header
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
                content.newLineAtOffset(50, y);
                content.showText("Directions:");
                content.endText();

                y -= 20;

                // Directions
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                content.newLineAtOffset(60, y);

                int step = 1;

                for (RecipeDirectionsDto direction : recipe.recipeDirections()) {

                    content.showText(step + ". " + direction.description());

                    content.newLineAtOffset(0, -20);
                    y -= 20;

                    step++;
                }

                content.endText();
            }

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            document.save(output);

            return output.toByteArray();
        }
    }
}
