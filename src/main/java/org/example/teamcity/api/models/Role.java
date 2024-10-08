package org.example.teamcity.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.teamcity.api.annotations.Parameterizable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Role extends BaseModel {
    @Parameterizable
    private String roleId;
    @Builder.Default
    private String scope = "g";

    public static final String PROJECT_ADMIN = "PROJECT_ADMIN";
    public static final String SYSTEM_ADMIN = "SYSTEM_ADMIN";
}
