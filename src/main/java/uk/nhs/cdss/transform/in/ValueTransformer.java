package uk.nhs.cdss.transform.in;

import org.hl7.fhir.dstu3.model.CoordinateResource;
import org.hl7.fhir.dstu3.model.PrimitiveType;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Type;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.cdss.domain.Coordinates;
import uk.nhs.cdss.transform.Transformer;

@Component
public class ValueTransformer implements Transformer<Type, Object> {

  @Override
  public Object transform(Type type) {
    if (type instanceof PrimitiveType) {
      return ((PrimitiveType) type).getValue();
    } else if (type instanceof Reference) {
      var resourceAnswer = ((Reference) type).getResource();

      return transformResourceAnswer(resourceAnswer);
    }
    return type;
  }

  private Object transformResourceAnswer(IBaseResource resourceAnswer) {
    if (resourceAnswer instanceof CoordinateResource) {
      return transformCoordinateAnswer((CoordinateResource) resourceAnswer);
    }

    return resourceAnswer;
  }

  private Coordinates transformCoordinateAnswer(CoordinateResource coordinateResource) {
    return Coordinates.builder()
        .x(coordinateResource.getXCoordinate().getValue())
        .y(coordinateResource.getYCoordinate().getValue())
        .build();
  }

}
