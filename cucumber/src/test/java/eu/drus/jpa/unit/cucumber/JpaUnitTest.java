package eu.drus.jpa.unit.cucumber;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.drus.jpa.unit.core.DecoratorRegistrar;
import eu.drus.jpa.unit.core.JpaUnitContext;
import eu.drus.jpa.unit.spi.ExecutionContext;
import eu.drus.jpa.unit.spi.TestClassDecorator;
import eu.drus.jpa.unit.spi.TestMethodDecorator;
import eu.drus.jpa.unit.spi.TestMethodInvocation;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("org.mockito.*")
@PrepareForTest({
        DecoratorRegistrar.class, JpaUnitContext.class
})
public class JpaUnitTest {

    @Mock
    private TestClassDecorator firstClassDecorator;

    @Mock
    private TestClassDecorator secondClassDecorator;

    @Mock
    private TestMethodDecorator firstMethodDecorator;

    @Mock
    private TestMethodDecorator secondMethodDecorator;

    @Mock
    private TestMethodInvocation invocation;

    @Mock
    private JpaUnitContext jpaUnitContext;

    private final Class<?> testClass = getClass();

    @Before
    public void prepareMocks() throws Exception {
        mockStatic(DecoratorRegistrar.class, JpaUnitContext.class);

        when(DecoratorRegistrar.getClassDecorators()).thenReturn(Arrays.asList(firstClassDecorator, secondClassDecorator));
        when(DecoratorRegistrar.getMethodDecorators()).thenReturn(Arrays.asList(firstMethodDecorator, secondMethodDecorator));

        when(JpaUnitContext.getInstance(any(Class.class))).thenReturn(jpaUnitContext);

        when(firstClassDecorator.isConfigurationSupported(any(ExecutionContext.class))).thenReturn(Boolean.TRUE);
        when(secondClassDecorator.isConfigurationSupported(any(ExecutionContext.class))).thenReturn(Boolean.TRUE);
        when(firstMethodDecorator.isConfigurationSupported(any(ExecutionContext.class))).thenReturn(Boolean.TRUE);
        when(secondMethodDecorator.isConfigurationSupported(any(ExecutionContext.class))).thenReturn(Boolean.TRUE);

        when(firstClassDecorator.getPriority()).thenReturn(1);
        when(secondClassDecorator.getPriority()).thenReturn(2);

        when(firstMethodDecorator.getPriority()).thenReturn(1);
        when(secondMethodDecorator.getPriority()).thenReturn(2);
    }

    @Test
    public void testProcessBeforeAllTestDecoratorExecutionOrder() throws Exception {
        // GIVEN
        final JpaUnit unit = new JpaUnit();

        // WHEN
        unit.processBeforeAll(getClass());

        // THEN
        final InOrder order = inOrder(firstClassDecorator, secondClassDecorator);
        order.verify(firstClassDecorator).beforeAll(notNull(ExecutionContext.class), eq(testClass));
        order.verify(secondClassDecorator).beforeAll(notNull(ExecutionContext.class), eq(testClass));
        verifyZeroInteractions(firstMethodDecorator, secondMethodDecorator);
    }

    @Test
    public void testProcessAfterAllTestDecoratorExecutionOrder() throws Exception {
        // GIVEN
        final JpaUnit unit = new JpaUnit();

        // WHEN
        unit.processAfterAll(getClass());

        // THEN
        final InOrder order = inOrder(secondClassDecorator, firstClassDecorator);
        order.verify(secondClassDecorator).afterAll(notNull(ExecutionContext.class), eq(testClass));
        order.verify(firstClassDecorator).afterAll(notNull(ExecutionContext.class), eq(testClass));
        verifyZeroInteractions(firstMethodDecorator, secondMethodDecorator);
    }

    @Test
    public void testProcessBeforeTestDecoratorExecutionOrder() throws Exception {
        // GIVEN
        final JpaUnit unit = new JpaUnit();

        // WHEN
        unit.processBefore(invocation);

        // THEN
        final InOrder order = inOrder(firstMethodDecorator, secondMethodDecorator);
        order.verify(firstMethodDecorator).beforeTest(notNull(TestMethodInvocation.class));
        order.verify(secondMethodDecorator).beforeTest(notNull(TestMethodInvocation.class));
        verifyZeroInteractions(firstClassDecorator, secondClassDecorator);
    }

    @Test
    public void testProcessAfterTestDecoratorExecutionOrder() throws Exception {
        // GIVEN
        final JpaUnit unit = new JpaUnit();

        // WHEN
        unit.processAfter(invocation);

        // THEN
        final InOrder order = inOrder(firstMethodDecorator, secondMethodDecorator);
        order.verify(secondMethodDecorator).afterTest(notNull(TestMethodInvocation.class));
        order.verify(firstMethodDecorator).afterTest(notNull(TestMethodInvocation.class));
        verifyZeroInteractions(firstClassDecorator, secondClassDecorator);
    }
}
