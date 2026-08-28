package com.matthew.recipe_backend.dtos;

import java.util.List;

public record ShareCookbookDto(List<Long> userIds, List<String> emails) {

}
