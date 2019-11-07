package uk.nhs.cdss.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.http.annotation.Obsolete;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseItemComponent;
import org.hl7.fhir.exceptions.FHIRException;
import org.springframework.stereotype.Service;
import uk.nhs.cdss.engine.CDSEngine;
import uk.nhs.cdss.entities.CodedDataEntity;
import uk.nhs.cdss.entities.DataRequirementEntity;
import uk.nhs.cdss.repos.DataRequirementRepository;
import uk.nhs.cdss.resourceBuilder.GuidanceResponseBuilder;
import uk.nhs.cdss.transform.EvaluationParameters;
import uk.nhs.cdss.transform.Transformers.CDSInputTransformer;
import uk.nhs.cdss.transform.Transformers.CDSOutputTransformer;
import uk.nhs.cdss.transform.bundle.CDSInputBundle;
import uk.nhs.cdss.transform.bundle.CDSOutputBundle;

@Service
public class EvaluateService {

  private CDSEngine rulesEngine;
  private CDSInputTransformer inputTransformer;
  private CDSOutputTransformer outputTransformer;
  private GuidanceResponseBuilder guidanceResponseBuilder;
  private DataRequirementRepository dataRequirementRepository;

  private boolean noSelection = false;

  public EvaluateService(CDSEngine rulesEngine,
      CDSInputTransformer inputTransformer,
      CDSOutputTransformer outputTransformer,
      GuidanceResponseBuilder guidanceResponseBuilder,
      DataRequirementRepository dataRequirementRepository) {
    this.rulesEngine = rulesEngine;
    this.inputTransformer = inputTransformer;
    this.outputTransformer = outputTransformer;
    this.guidanceResponseBuilder = guidanceResponseBuilder;
    this.dataRequirementRepository = dataRequirementRepository;
  }

  public GuidanceResponse getGuidanceResponseNew(Parameters parameters, Long serviceDefinitionId) {
    var evaluationParameters = new EvaluationParameters(parameters);
    var inputBundle = new CDSInputBundle(serviceDefinitionId, evaluationParameters);
    var input = inputTransformer.transform(inputBundle);

    var output = rulesEngine.evaluate(input);

    var outputBundle = new CDSOutputBundle(output, serviceDefinitionId, evaluationParameters);
    return outputTransformer.transform(outputBundle);
  }

  @Obsolete
  public GuidanceResponse getGuidanceResponse(Bundle bundle, Long serviceDefinitionId) {
    return getGuidanceResponseNew((Parameters)bundle.getEntry().get(0).getResource(), serviceDefinitionId);
  }

  @Obsolete
  public GuidanceResponse getGuidanceResponse(Parameters parameters, Long serviceDefinitionId) {
    noSelection = false;
    var evaluationParameters = new EvaluationParameters(parameters);

    List<DataRequirementEntity> matchedDataRequirementEntity = null;
    try {
      matchedDataRequirementEntity = getQuestionnaireDataRequirement(
          evaluationParameters.getResponses(),
          serviceDefinitionId);
    } catch (FHIRException e) {
      e.printStackTrace();
    }
    return guidanceResponseBuilder.buildGuidanceResponse(
        evaluationParameters.getRequestId(),
        serviceDefinitionId,
        evaluationParameters.getInputData(),
        matchedDataRequirementEntity,
        noSelection);
  }

  private List<DataRequirementEntity> getQuestionnaireDataRequirement(
      List<QuestionnaireResponse> responses,
      Long serviceDefinitionId) throws FHIRException {

    // Finds QuestionnaireResource parameter
    var responseItemComponents = responses.stream()
        .flatMap(response -> response.getItem().stream())
        .collect(Collectors.toList());

    if (responseItemComponents.isEmpty()) { // Return null if no QuestionnaireResponse exists in params
      return null;
    }

    List<DataRequirementEntity> dataRequirementEntityList = new ArrayList<>();

    for (QuestionnaireResponseItemComponent questionnaireResponseItemComponent : responseItemComponents) {

      // Get ID of questionnaire from response
      long questionnaireId = Long.parseLong(questionnaireResponseItemComponent.getLinkId());
      if (serviceDefinitionId == 8L) {
        // check if questionnaireId matches the relevant questionnaire(None of the above
        // example)
        // if so, check if none of the above has been selected
        // get all relevant observations
        // return
        dataRequirementEntityList = new ArrayList<>();
        if (questionnaireResponseItemComponent.getAnswerFirstRep().getValueCoding().getDisplay()
            .equalsIgnoreCase("None of the above")) {
          noSelection = true;
        }
      } else if (serviceDefinitionId == 7L && questionnaireId == 41L) {
        String coords = questionnaireResponseItemComponent.getAnswerFirstRep().getValueStringType()
            .asStringValue();
        String[] coordArray = coords.substring(coords.indexOf(":") + 1).split(",");

        // create colour code
        Coding coding = new Coding();
        CodeableConcept cc = new CodeableConcept();

        if (Integer.parseInt(coordArray[0]) < 225) {
          if (Integer.parseInt(coordArray[1]) < 200) {
            coding.setCode("405738005");
            coding.setDisplay("Blue");
          } else {
            coding.setCode("371240000");
            coding.setDisplay("Red");
          }
        } else {
          if (Integer.parseInt(coordArray[1]) < 200) {
            coding.setCode("90998002");
            coding.setDisplay("Yellow");
          } else {
            coding.setCode("405739002");
            coding.setDisplay("Green");
          }
        }

        // set on answer
        cc.addCoding(coding);
        questionnaireResponseItemComponent.getAnswerFirstRep().setValue(coding);

      }

      dataRequirementEntityList.add(dataRequirementRepository
          .findDistinctByServiceDefinitionIdAndQuestionnaireId(serviceDefinitionId, questionnaireId));

    }

    if (serviceDefinitionId == 8L) {
      for (DataRequirementEntity dataRequirementEntity : dataRequirementEntityList) {
        for (CodedDataEntity codedDataEntity : dataRequirementEntity.getCodedData()) {
          String snomedCode = translateResponseToSnomed(codedDataEntity.getDisplay());
          try {
            for (QuestionnaireResponseItemComponent itemComponent : responseItemComponents) {
              String answerDisplay = itemComponent.getAnswerFirstRep().getValueCoding().getDisplay();
              if (answerDisplay.equalsIgnoreCase(snomedCode)) {
                codedDataEntity.setValue(true);
              }
            }
          } catch (Exception e) {
            codedDataEntity.setValue(false);
          }
        }
      }
    }

    // Check if service (CDSS Switch) has been selected and set the value
    // boolean property according to the answer selected
    if (serviceDefinitionId == 10L) {
      for (DataRequirementEntity dataRequirementEntity : dataRequirementEntityList) {
        for (CodedDataEntity codedDataEntity : dataRequirementEntity.getCodedData()) {
          try {
            for (QuestionnaireResponseItemComponent itemComponent : responseItemComponents) {
              String answerDisplay = itemComponent.getAnswerFirstRep().getValueCoding().getDisplay();
              if ("Cough".equalsIgnoreCase(answerDisplay)) {
                System.out.println("match - setting true");
                codedDataEntity.setValue(true);
              } else {
                System.out.println("no match - setting true");
                codedDataEntity.setValue(false);
              }
            }
          } catch (Exception e) {
            codedDataEntity.setValue(false);
            System.out.println("match - setting false");
          }
        }
      }
    }

    return dataRequirementEntityList;
  }

  // Choking and unable to stop or having trouble breathing or feeling that the
  // airways are getting blocked
  private String translateResponseToSnomed(String response) {
    switch (response) {
      case "Choking due to airways obstruction":
        return "Choking and unable to stop or having trouble breathing or feeling that the airways are getting blocked";
      case "Unable to swallow saliva":
        return "Breathing noisily or unable to swallow saliva";
      case "Difficulty breathing":
        return "Too breathless to speak or gasping for breath";
      case "Blue lips":
        return "Turning blue around the mouth or lips";
      case "Feels unwell":
        return "Feeling unwell and skin feels cold and sweaty or is very pale or blotchy";
      case "Unconscious":
        return "Unconscious or hard to wake up or keep awake";
      default:
        return null;
    }

  }

}
