package com.khutircraftubackend.product.image.response;

import com.fasterxml.jackson.annotation.JsonView;
//import com.khutircraftubackend.common.validation.Public;
import lombok.*;

//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@JsonView(Public.class)
@Builder
public record ImageLinks (
     String thumbnail,
     String small,
     String medium,
     String large
){
}
