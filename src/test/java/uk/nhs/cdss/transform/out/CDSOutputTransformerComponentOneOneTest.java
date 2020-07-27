package uk.nhs.cdss.transform.out;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.cdss.testHelpers.fixtures.CDSOutputBundleFixture;
import uk.nhs.cdss.services.ReferenceStorageService;
import uk.nhs.cdss.transform.bundle.CDSOutputBundle;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(properties = { "spring.profiles.active=1.1", "cognito.user.pool=" })
public class CDSOutputTransformerComponentOneOneTest {

  @Autowired
  private CDSOutputTransformer outputTransformer;

  @MockBean
  private ReferenceStorageService mockStorageService;

  @Before
  public void setup() {
    when(mockStorageService.create(any())).thenReturn(new Reference(randomAlphabetic(10)));
  }

  @Test
  public void shouldSaveValidOnePointOneResources() {
    CDSOutputBundle cdsOutputBundle = CDSOutputBundleFixture.testOutputBundle();

    outputTransformer.transform(cdsOutputBundle);

    var referralRequestCaptor = ArgumentCaptor.forClass(ReferralRequest.class);
    verify(mockStorageService, atLeastOnce()).create(referralRequestCaptor.capture());

    ReferralRequest actual = referralRequestCaptor.getValue();
    // CDS API v1.1 has a reason code
    assertThat(actual.hasReasonCode(), is(true));
  }

}
