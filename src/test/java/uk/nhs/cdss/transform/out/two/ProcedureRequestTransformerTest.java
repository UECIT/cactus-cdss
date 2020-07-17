package uk.nhs.cdss.transform.out.two;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.cdss.engine.CodeDirectory;
import uk.nhs.cdss.services.ReferenceStorageService;
import uk.nhs.cdss.transform.out.ConceptTransformer;

@RunWith(MockitoJUnitRunner.class)
public class ProcedureRequestTransformerTest {

  @InjectMocks
  private ProcedureRequestTransformer procedureRequestTransformer;

  @Mock
  private CodeDirectory codeDirectory;
  @Mock
  private ConceptTransformer conceptTransformer;
  @Mock
  private ReferenceStorageService storageService;

}