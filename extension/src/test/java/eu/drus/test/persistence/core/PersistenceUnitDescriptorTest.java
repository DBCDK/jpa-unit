package eu.drus.test.persistence.core;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PersistenceUnitDescriptorTest {

    private static final String DDL_GENERATION_VALUE = "drop-and-create-tables";
    private static final String ATTRIBUTE_DDL_GENERATION = "eclipselink.ddl-generation";
    private static final String USER_VALUE = "test";
    private static final String PASSWORD_VALUE = "test";
    private static final String URL_VALUE = "jdbc:h2:mem:serviceEnablerDB;DB_CLOSE_DELAY=-1";
    private static final String DRIVER_VALUE = "org.h2.Driver";
    private static final String ATTRIBUTE_PASSWORD = "javax.persistence.jdbc.password";
    private static final String ATTRIBUTE_USER = "javax.persistence.jdbc.user";
    private static final String ATTRIBUTE_URL = "javax.persistence.jdbc.url";
    private static final String ATTRIBUTE_DRIVER = "javax.persistence.jdbc.driver";
    private static final String PERSISTENCE_UNIT_NAME = "pu-name";
    private static final String PROVIDER_CLASS = "some class name";

    private Element puElement;

    @Before
    public void prepareDocument() throws ParserConfigurationException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();

        final Document document = builder.newDocument();

        puElement = document.createElement("persistence-unit");
        puElement.setAttribute("name", PERSISTENCE_UNIT_NAME);
        puElement.setAttribute("transaction-type", "RESOURCE_LOCAL");
        document.appendChild(puElement);

        final Element providerElement = document.createElement("provider");
        providerElement.setTextContent(PROVIDER_CLASS);
        puElement.appendChild(providerElement);

        final Element propsElement = document.createElement("properties");
        puElement.appendChild(propsElement);

        final Element driverPropElement = document.createElement("property");
        driverPropElement.setAttribute("name", ATTRIBUTE_DRIVER);
        driverPropElement.setAttribute("value", DRIVER_VALUE);
        propsElement.appendChild(driverPropElement);

        final Element urlPropElement = document.createElement("property");
        urlPropElement.setAttribute("name", ATTRIBUTE_URL);
        urlPropElement.setAttribute("value", URL_VALUE);
        propsElement.appendChild(urlPropElement);

        final Element userNamePropElement = document.createElement("property");
        userNamePropElement.setAttribute("name", ATTRIBUTE_USER);
        userNamePropElement.setAttribute("value", USER_VALUE);
        propsElement.appendChild(userNamePropElement);

        final Element userPassPropElement = document.createElement("property");
        userPassPropElement.setAttribute("name", ATTRIBUTE_PASSWORD);
        userPassPropElement.setAttribute("value", PASSWORD_VALUE);
        propsElement.appendChild(userPassPropElement);

        final Element ddlGenPropElement = document.createElement("property");
        ddlGenPropElement.setAttribute("name", ATTRIBUTE_DDL_GENERATION);
        ddlGenPropElement.setAttribute("value", DDL_GENERATION_VALUE);
        propsElement.appendChild(ddlGenPropElement);
    }

    @Test
    public void testPersistenceUnitDescriptorWithoutUnitNameSet() {
        // GIVEN
        // the created document and
        puElement.removeAttribute("name");

        // WHEN
        final PersistenceUnitDescriptor descriptor = new PersistenceUnitDescriptor(puElement, Collections.emptyMap());

        // THEN
        assertThat(descriptor.getUnitName(), nullValue());
        assertThat(descriptor.getProperties().size(), equalTo(5));
        assertThat(descriptor.getProperties(), hasEntry(ATTRIBUTE_DRIVER, DRIVER_VALUE));
        assertThat(descriptor.getProperties(), hasEntry(ATTRIBUTE_URL, URL_VALUE));
        assertThat(descriptor.getProperties(), hasEntry(ATTRIBUTE_USER, USER_VALUE));
        assertThat(descriptor.getProperties(), hasEntry(ATTRIBUTE_PASSWORD, PASSWORD_VALUE));
        assertThat(descriptor.getProperties(), hasEntry(ATTRIBUTE_DDL_GENERATION, DDL_GENERATION_VALUE));
        assertThat(descriptor.getProviderClassName(), equalTo(PROVIDER_CLASS));
    }

    @Test
    public void testPersistenceUnitDescriptorWithoutOverwrittenProperties() {
        // GIVEN
        // the created document

        // WHEN
        final PersistenceUnitDescriptor descriptor = new PersistenceUnitDescriptor(puElement, Collections.emptyMap());

        // THEN
        assertThat(descriptor.getUnitName(), equalTo(PERSISTENCE_UNIT_NAME));
        assertThat(descriptor.getProperties().size(), equalTo(5));
        assertThat(descriptor.getProperties(), hasEntry(ATTRIBUTE_DRIVER, DRIVER_VALUE));
        assertThat(descriptor.getProperties(), hasEntry(ATTRIBUTE_URL, URL_VALUE));
        assertThat(descriptor.getProperties(), hasEntry(ATTRIBUTE_USER, USER_VALUE));
        assertThat(descriptor.getProperties(), hasEntry(ATTRIBUTE_PASSWORD, PASSWORD_VALUE));
        assertThat(descriptor.getProperties(), hasEntry(ATTRIBUTE_DDL_GENERATION, DDL_GENERATION_VALUE));
        assertThat(descriptor.getProviderClassName(), equalTo(PROVIDER_CLASS));
    }

    @Test
    public void testPersistenceUnitDescriptorWithOverwrittenProperties() {
        // GIVEN
        // the created document and the following properties
        final String urlValue = "some url";
        final String driverValue = "some driver";
        final String passValue = "some pass";

        final String someFurtherProp = "some other prop";
        final String someFurtherValue = "some other value";

        final Map<String, Object> userProperties = new HashMap<>();
        userProperties.put(ATTRIBUTE_URL, urlValue);
        userProperties.put(ATTRIBUTE_DRIVER, driverValue);
        userProperties.put(ATTRIBUTE_PASSWORD, passValue);
        userProperties.put(someFurtherProp, someFurtherValue);

        // WHEN
        final PersistenceUnitDescriptor descriptor = new PersistenceUnitDescriptor(puElement, userProperties);

        // THEN
        assertThat(descriptor.getUnitName(), equalTo(PERSISTENCE_UNIT_NAME));
        assertThat(descriptor.getProperties().size(), equalTo(6));
        assertThat(descriptor.getProperties(), hasEntry(ATTRIBUTE_DRIVER, driverValue));
        assertThat(descriptor.getProperties(), hasEntry(ATTRIBUTE_URL, urlValue));
        assertThat(descriptor.getProperties(), hasEntry(ATTRIBUTE_USER, USER_VALUE));
        assertThat(descriptor.getProperties(), hasEntry(ATTRIBUTE_PASSWORD, passValue));
        assertThat(descriptor.getProperties(), hasEntry(ATTRIBUTE_DDL_GENERATION, DDL_GENERATION_VALUE));
        assertThat(descriptor.getProperties(), hasEntry(someFurtherProp, someFurtherValue));
        assertThat(descriptor.getProviderClassName(), equalTo(PROVIDER_CLASS));
    }
}
