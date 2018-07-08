package com.chernyshov777.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Amount {
    @NotNull
    private double value;
    @NotNull
    private String currency;
}
