/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * Projection DTO for the {@link org.sigmah.shared.domain.Site} domain object, including
 * its {@link org.sigmah.shared.domain.Location Location}, and
 * {@link org.sigmah.shared.domain.ReportingPeriod ReportingPeriod} totals
 *
 * @author Alex Bertram
 */
public final class SiteDTO extends BaseModelData implements EntityDTO {

	
    public static final String ENTITY_NAME = "Site";

	public SiteDTO() {
	}

    /**
     * Constucts an empty SiteDTO with the given id
     *
     * @param id the siteId
     */
    public SiteDTO(int id) {
        setId(id);
    }

    /**
     * Constructs a shallow copy of the given SiteDTO instance
     *
     * @param site the object to copy
     */
    public SiteDTO(SiteDTO site) {
        super(site.getProperties());
    }

    /**
     * Sets this site's id
     *
     * @param id
     */
    public void setId(int id) {
		set("id", id);
	}

    /**
     *
     * @return this site's id
     */
	public int getId() {
		return (Integer)get("id");
	}

    /**
     * @return the id of the Activity to which this Site belongs
     */
	public int getActivityId() {
		return (Integer)get("activityId");
	}

    /**
     * Sets the id of Activity to which this Site belongs
     *
     * @param id
     */
	public void setActivityId(int id) {
		set("activityId", id);
	}

    /**
     * @return the beginning of work at this Site
     */
	public Date getDate1() {
		return get("date1");
	}

    /**
     * Sets the beginning of work at this Site
     *
     * @param date1
     */
	public void setDate1(Date date1) {
		set("date1", date1);
	}

    /**
     * @return the end of work at this Site
     */
	public Date getDate2() {
		return get("date2");
	}

    /**
     * Sets the end of work at this Site
     *
     * @param date2
     */
	public void setDate2(Date date2) {
		set("date2", date2);
	}

    /**
     * 
     * @return the name of the Partner who owns this Site
     */
    public String getPartnerName() {
		PartnerDTO partner = getPartner();
		if(partner == null) {
			return null;
		} 
		
		return partner.getName();
	}
    
    
    public String getProjectName() {
		return getProject() == null ? "" : getProject().getName();
    }

    /**
     * @return the instance of the Partner who owns this Site
     */
	public PartnerDTO getPartner() {
		return get("partner");
	}

    /**
     * Sets the partner who owns this Site
     * @param partner
     */
	public void setPartner(PartnerDTO partner) {
		set("partner", partner);
	}

    /**
     * Sets the name of Location of this Site.
     * See {@link org.sigmah.shared.domain.Location#getName()}
     * 
     * @param name the name of the location.
     */
	public void setLocationName(String name) {
		set("locationName", name);
	}

    /**
     * @return  the name of the Location of the Site
     */
	public String getLocationName() {
		return get("locationName");
	}

    /**
     *
     * @return the "axe routier" on which the Location of the Site lies
     */
	public String getLocationAxe() {
		return get("locationAxe");
	}

    /**
     * Sets the axe routier on which the Location of the Site lies
     * @param name
     */
	public void setLocationAxe(String name) {
		set("locationAxe", name);
	}

	public void setAdminEntity(int levelId, AdminEntityDTO value) {
		set(AdminLevelDTO.getPropertyName(levelId), value);
	}

	public AdminEntityDTO getAdminEntity(int levelId) {
		return get(AdminLevelDTO.getPropertyName(levelId));
	}
	
	public Object getAdminEntityName(int levelId) {
		AdminEntityDTO entity = getAdminEntity(levelId);
		if(entity == null) {
            return null;
        }
		
		return entity.getName();
	}


    /**
     * Sets the X (longitudinal) coordinate of this Site
     * @param x the longitude, in degrees
     */
	public void setX(Double x) {
		set("x", x);
	}

    /**
     *
     * @return  the X (longitudinal) coordinate of this Site, or null if a coordinate has
     * not been set.
     */
	public Double getX() {
		return get("x");
	}

    /**
     * @return the Y (latitudinal) coordinate of this Site in degrees, or null if a coordinate
     * has not been set.
     */
	public Double getY() {
		return get("y");
	}

    /**
     * Sets the Y (latitudinal) coordinate of this Site in degrees
     *
     * @param y latitude in degrees
     */
	public void setY(Double y) { 
		set("y", y);
	}

    /**
     * @return  true if this Site has non-null x and y coordinates.
     */
	public boolean hasCoords() {
		return get("x")!=null && get("y")!=null;
	}

    /**
     * Sets the value for the given Attribute of this Site
     * @param attributeId the Id of the attribute
     * @param value
     */
	public void setAttributeValue(int attributeId, Boolean value) {
		set(AttributeDTO.getPropertyName(attributeId), value);
	}

    /**
     * Sets the (total) value of the given Indicator of this Site
     *
     * @param indicatorId  the Id of the indicator
     * @param value the total value for all ReportingPeriods
     */
	public void setIndicatorValue(int indicatorId, Double value) {
		set(IndicatorDTO.getPropertyName(indicatorId), value);
	}

    /**
     *
     * @param indicatorId
     * @return the total value of the given indicator for this Site,
     * across all ReportingPeriods
     */
	public Double getIndicatorValue(int indicatorId) {
		return get(IndicatorDTO.getPropertyName(indicatorId));
	}

    /**
     *
     * @param indicator
     * @return the total value of the given indicator for this Site, across
     * all ReportingPeriods
     */
    public Double getIndicatorValue(IndicatorDTO indicator) {
        return getIndicatorValue(indicator.getId());
    }

    /**
     * Sets the plain text comments for this Site
     *
     * @param comments comments in plain-text format
     */
	public void setComments(String comments) {
		set("comments", comments);
	}

    /**
     *
     * @return  the plain-text comments for this Site
     */
	public String getComments() {
		return get("comments");
	}

    /**
     * @param attributeId
     * @return the value of the given attribute for this Site
     */
	public Boolean getAttributeValue(int attributeId) {
		return get(AttributeDTO.getPropertyName(attributeId));
	}


    /**
     * Tests equality based on id
     *
     * @param o
     * @return true if the given Site has the same Id as this Site
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SiteDTO siteModel = (SiteDTO) o;
        if (getId() != siteModel.getId()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return getId();
    }

    /**
     *
     * @return true if this Site has a non-null ID
     */
    public boolean hasId() {
        return get("id") != null;
    }

    @Override
    public String getEntityName() {
        return ENTITY_NAME;
    }
    
	public ProjectDTO getProject() {
		return (ProjectDTO)get("project");
	}
	
	public void setProject(ProjectDTO project) {
		set("project", project);
	}
	
	/*
	 * Returns true when this Site is locked for C/U/D operations
	 */
	public boolean fallsWithinLockedPeriod(ActivityDTO activity) {
		return fallsWithinLockedPeriods(getRelevantLockedPeriods(activity), activity);
	}

	private List<LockedPeriodDTO> getRelevantLockedPeriods(ActivityDTO activity) {
		List<LockedPeriodDTO> lockedPeriods = new ArrayList<LockedPeriodDTO>();
		
		lockedPeriods.addAll(activity.getEnabledLockedPeriods());
		if (activity.getDatabase() != null) {
			lockedPeriods.addAll(activity.getDatabase().getEnabledLockedPeriods());
		}
		if (getProject() != null) {
			lockedPeriods.addAll(getProject().getEnabledLockedPeriods());
		}
		
		return lockedPeriods;
	}
	
	/*
	 * Returns true when this Site falls within at least one of given LockedPeriods
	 */
	public boolean fallsWithinLockedPeriods(Iterable<LockedPeriodDTO> lockedPeriods, ActivityDTO activity) {
		for (LockedPeriodDTO lockedPeriod : lockedPeriods) {
			// For reporting purposes, only the Date2 is 'counted'.  
			if (lockedPeriod.fallsWithinPeriod(getDate2())) {
				return true;
			}
		}
		
		return false;
	}
	
	public Set<LockedPeriodDTO> getAffectedLockedPeriods(ActivityDTO activity) {
		Set<LockedPeriodDTO> affectedLockedPeriods = new HashSet<LockedPeriodDTO>();
		
		for (LockedPeriodDTO lockedPeriod : getRelevantLockedPeriods(activity)) {
			// For reporting purposes, only the Date2 is 'counted'.  
			if (lockedPeriod.fallsWithinPeriod(getDate2())) {
				affectedLockedPeriods.add(lockedPeriod);
			}
		}
		
		return affectedLockedPeriods;
	}
}
