package com.uj.stxtory.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutoMangerListDto {
    private List<AutoMangerDto>  autoMangers;
}
