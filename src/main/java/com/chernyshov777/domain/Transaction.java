package com.chernyshov777.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @NotNull
    @JsonProperty("external_id")
    private long externalId;
    @NotNull
    private Amount amount;
    @NotNull
    private String description;
}
