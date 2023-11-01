package com.example.demoapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
class Gender {
    String gender;
    String name;
    int count;
    double probability;
}

