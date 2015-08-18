package amberdb.model.builder;

import amberdb.model.EADWork;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.*;
public class ComponentSubUnitBuilderTest {

    EADWork componentWork;
    ComponentSubUnitBuilder componentSubUnitBuilder;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() {
        componentWork = mock(EADWork.class);
        componentSubUnitBuilder = new ComponentSubUnitBuilder();
    }

    @Test
    public void subUnitTypeNotSetWhenComponentLevelIsEmpty() {
        componentWork = componentSubUnitBuilder.setSubUnitAndBibLevelFields(componentWork, "N/A", "");
        verify(componentWork, never()).setSubUnitType("Collection");
    }

    @Test
    public void subUnitTypeNotSetWhenComponentLevelIsNull() {
        componentWork = componentSubUnitBuilder.setSubUnitAndBibLevelFields(componentWork, "N/A", null);
        verify(componentWork, never()).setSubUnitType("Collection");
    }

    @Test
    public void subUnitTypeIsSetOnComponentWork() {
        componentWork = componentSubUnitBuilder.setSubUnitAndBibLevelFields(componentWork, "N/A", "Collection");
        verify(componentWork).setSubUnitType("Collection");
    }

    @Test
    public void bibLevelIsSetOnComponentWork() {
        componentWork = componentSubUnitBuilder.setSubUnitAndBibLevelFields(componentWork, "N/A", "Collection");
        verify(componentWork).setBibLevel("Set");
    }

    @Test
    public void bibLevelIsSetToItemWhenComponentWorkIsItem() {
        componentWork = componentSubUnitBuilder.setSubUnitAndBibLevelFields(componentWork, "N/A", "Item");
        verify(componentWork).setBibLevel("Item");
    }

    @Test
    public void bibLevelIsSetToPartWhenComponentWorkIsOtherlevel() {
        componentWork = componentSubUnitBuilder.setSubUnitAndBibLevelFields(componentWork, "N/A", "Otherlevel");
        verify(componentWork).setBibLevel("Part");
    }

    @Test
    public void throwsErrorWhenInvalidComponentLevelIsPassedIn() {
        expectedEx.expect(EADValidationException.class);
        expectedEx.expectMessage("INVALID_SUB_UNIT_TYPE");

        componentSubUnitBuilder.setSubUnitAndBibLevelFields(componentWork, "N/A", "InvalidComponentLevel");
    }

}