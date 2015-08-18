package amberdb.model.builder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EADDuplicateValidatorTest {

    XmlDocumentParser parser;
    EADDuplicateValidator eadDuplicateValidator;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        parser = mock(XmlDocumentParser.class);
        eadDuplicateValidator = new EADDuplicateValidator(parser);
    }

    @Test
    public void emptyUUIDListFromParserIsValid() {
        when(parser.getUUIDsAsList(100)).thenReturn(new ArrayList<String>());
        eadDuplicateValidator.validate();
    }

    @Test
    public void UUIDListWithUniqueElementsIsValid() {
        when(parser.getUUIDsAsList(100)).thenReturn(new ArrayList(asList("1", "2", "3")));
        eadDuplicateValidator.validate();
    }

    @Test
    public void UUIDListWithDuplicateElementsIsThrowsError() {
        expectedEx.expect(EADValidationException.class);
        expectedEx.expectMessage("DUPLICATE_UUID");
        when(parser.getUUIDsAsList(100)).thenReturn(new ArrayList(asList("1", "1", "2")));

        eadDuplicateValidator.validate();
    }

    @Test
    public void UUIDListWithDuplicateEmptyElementsIsThrowsMissingUUIDError() {
        expectedEx.expect(EADValidationException.class);
        expectedEx.expectMessage("MISSING_UUID_DETECTED");
        when(parser.getUUIDsAsList(100)).thenReturn(new ArrayList(asList("", "", "2")));

        eadDuplicateValidator.validate();
    }
}