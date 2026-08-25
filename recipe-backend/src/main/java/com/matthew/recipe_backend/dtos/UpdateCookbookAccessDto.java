package com.matthew.recipe_backend.dtos;

import java.util.List;

public record UpdateCookbookAccessDto(List<SharedUserDto> users) {

}
