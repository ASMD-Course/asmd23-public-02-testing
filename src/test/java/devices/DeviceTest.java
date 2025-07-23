package devices;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeviceTest {

    @Mock FailingPolicy stubFailingPolicy;
    @Spy RandomFailing spyRandomPolicy;


    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class DeviceInitialization {
        @Test
        @DisplayName("Device must specify a strategy in order to work")
        void testNonNullStrategy() {
            assertThrows(NullPointerException.class, () -> new StandardDevice(null));
        }

       @Test
       @DisplayName("Device should be initialized with an off status")
       void testInitiallyOff() {
           Device device = new StandardDevice(stubFailingPolicy);
           assertFalse(device.isOn());
       }
    }

    @Nested
    class DeviceReset {
        private Device device;

        @Test
        @DisplayName("Device must be put to off state when I reset it")
        void testOff() {
            device = new StandardDevice(stubFailingPolicy);
            device.reset();
            assertFalse(device.isOn());
        }

        @Test
        @DisplayName("Reset in FailingPolicy is called correctly")
        void testCallFailingPolicyReset() {
            device = new StandardDevice(spyRandomPolicy);
            device.reset();
            verify(spyRandomPolicy).reset();
        }
    }


    @Nested
    class SwitchingOff {
        private Device device;

        @BeforeEach
        void init() {
            device = new StandardDevice(stubFailingPolicy);
        }

        @Test
        @DisplayName("Switching off should set the status correctly")
        void testSwitchingOff() {
            when(stubFailingPolicy.attemptOn()).thenReturn(true);
            device.on();
            device.off();
            assertFalse(device.isOn());
        }

        @Test
        @DisplayName("Switching off multiple time should return the same off status")
        void testSwitchingOffMultipleTime() {
            IntStream.range(0, 2).forEach(i -> {
                device.off();
                assertFalse(device.isOn());
            });
        }
    }


    @Nested
    class SwitchingOn {
        private Device device;

        @BeforeEach
        void init() {
            device = new StandardDevice(stubFailingPolicy);
            when(stubFailingPolicy.attemptOn()).thenReturn(true, true, false);
        }

        @Test
        @DisplayName("Device switch on and off until failing")
        void testSwitchesOnAndOff() {
            IntStream.range(0, 2).forEach(i -> {
                device.on();
                assertTrue(device.isOn());
                device.off();
                assertFalse(device.isOn());
            });
            assertThrows(IllegalStateException.class, () -> device.on());
        }

        @Test
        @DisplayName("AttemptOn is called as expected")
        void testAttemptOn() {
            device.isOn();
            verifyNoInteractions(stubFailingPolicy);
            try{
                device.on();
            } catch (IllegalStateException e){}
            verify(stubFailingPolicy).attemptOn();
            device.reset();
            assertEquals(2, Mockito.mockingDetails(stubFailingPolicy).getInvocations().size());
        }

        @Test
        @DisplayName("attemptOn is called as expected")
        void testAttemptMultipleExecutions() {
            verify(stubFailingPolicy, times(0)).attemptOn();
            device.on();
            verify(stubFailingPolicy, times(1)).attemptOn();
            assertTrue(device.isOn());

            device.off();
            verify(stubFailingPolicy, times(1)).attemptOn();
            device.on();
            verify(stubFailingPolicy, times(2)).attemptOn();
            assertTrue(device.isOn());

            device.off();
            verify(stubFailingPolicy, times(2)).attemptOn();
            assertThrows(IllegalStateException.class, () -> device.on());
            verify(stubFailingPolicy, times(3)).attemptOn();
        }
    }
}
