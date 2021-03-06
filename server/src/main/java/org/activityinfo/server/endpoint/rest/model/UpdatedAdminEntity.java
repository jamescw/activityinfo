package org.activityinfo.server.endpoint.rest.model;

import com.vividsolutions.jts.geom.Geometry;
import org.activityinfo.server.database.hibernate.entity.Bounds;

public class UpdatedAdminEntity {
    private Integer id;
    private Integer parentId;
    private String name;
    private String code;
    private Bounds bounds;
    private boolean deleted;
    private Geometry geometry;

    public Integer getId() {
        return id;
    }

    public boolean isNew() {
        return id == null || id == 0;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

}
