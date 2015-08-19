package amberdb.model.builder;

import amberdb.model.EADWork;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.*;
public class ComponentLevelMapperTest {

    EADWork componentWork;
    ComponentLevelMapper componentLevelMapper;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() {
        componentWork = mock(EADWork.class);
        componentLevelMapper = new ComponentLevelMapper();
    }

    @Test
    public void subUnitTypeNotSetWhenComponentLevelIsEmpty() {
        componentWork = componentLevelMapper.setSubUnitAndBibLevelFields(componentWork, "N/A", "");
        verify(componentWork, never()).setSubUnitType("Collection");
    }

    @Test
    public void subUnitTypeNotSetWhenComponentLevelIsNull() {
        componentWork = componentLevelMapper.setSubUnitAndBibLevelFields(componentWork, "N/A", null);
        verify(componentWork, never()).setSubUnitType("Collection");
    }

    @Test
    public void subUnitTypeIsSetOnComponentWork() {
        componentWork = componentLevelMapper.setSubUnitAndBibLevelFields(componentWork, "N/A", "Collection");
        verify(componentWork).setSubUnitType("Collection");
    }

    @Test
    public void bibLevelIsSetOnComponentWork() {
        componentWork = componentLevelMapper.setSubUnitAndBibLevelFields(componentWork, "N/A", "Collection");
        verify(componentWork).setBibLevel("Set");
    }

    @Test
    public void bibLevelIsSetToItemWhenComponentWorkIsItem() {
        componentWork = componentLevelMapper.setSubUnitAndBibLevelFields(componentWork, "N/A", "Item");
        verify(componentWork).setBibLevel("Item");
    }

    @Test
    public void bibLevelIsSetToPartWhenComponentWorkIsOtherlevel() {
        componentWork = componentLevelMapper.setSubUnitAndBibLevelFields(componentWork, "N/A", "Otherlevel");
        verify(componentWork).setBibLevel("Part");
    }

    @Test
    public void throwsErrorWhenInvalidComponentLevelIsPassedIn() {
        expectedEx.expect(EADValidationException.class);
        expectedEx.expectMessage("INVALID_SUB_UNIT_TYPE");

        componentLevelMapper.setSubUnitAndBibLevelFields(componentWork, "N/A", "InvalidComponentLevel");
    }

}