package org.activityinfo.server.endpoint.odk.xform;

import com.google.common.collect.Lists;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import java.util.List;

public class Body {

    private final List<BodyElement> elements = Lists.newArrayList();

    @XmlElements({
            @XmlElement(name = "group", namespace = Namespaces.XFORM, type = Group.class),
            @XmlElement(name = "select1", namespace = Namespaces.XFORM, type = Select1.class),
            @XmlElement(name = "select", namespace = Namespaces.XFORM, type = Select.class),
            @XmlElement(name = "input", namespace = Namespaces.XFORM, type = Input.class),
            @XmlElement(name = "upload", namespace = Namespaces.XFORM, type = Upload.class)
    })
    public List<BodyElement> getElements() {
        return elements;
    }

    public void addElement(BodyElement bodyElement) {
        elements.add(bodyElement);
    }
}