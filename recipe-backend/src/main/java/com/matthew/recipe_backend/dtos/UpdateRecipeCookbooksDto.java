package com.matthew.recipe_backend.dtos;

import java.util.List;

public record UpdateRecipeCookbooksDto(List<CookbookUpdateDto> cookbookUpdates) {

}
