package org.activityinfo.ui.client.component.form.field.hierarchy;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.promise.Promise;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Models a hierarchy of choices for the user
 */
public class Hierarchy {

    private Map<ResourceId, Level> levelMap = Maps.newHashMap();
    private List<Level> roots = Lists.newArrayList();
    private List<Level> levels = Lists.newArrayList();


    public static Promise<Hierarchy> get(final ResourceLocator resourceLocator, ReferenceType type) {
        return Promise.map(type.getRange(), new Function<ResourceId, Promise<FormClass>>() {
            @Override
            public Promise<FormClass> apply(@Nullable ResourceId input) {
                return resourceLocator.getFormClass(input);
            }
        }).then(new Function<List<FormClass>, Hierarchy>() {
            @Nullable
            @Override
            public Hierarchy apply(@Nullable List<FormClass> input) {
                return new Hierarchy(input);
            }
        });
    }

    public Hierarchy(List<FormClass> rangeFormClasses) {

        // Find all of the form class here
        for(FormClass formClass : rangeFormClasses) {
            if(!levelMap.containsKey(formClass.getId())) {
                levelMap.put(formClass.getId(), new Level(formClass));
            }
        }

        // Assign parents...
        for(Level level : levelMap.values()) {
            if(!level.isRoot()) {
                level.parent = levelMap.get(level.parentId);
                level.parent.children.add(level);
            }
        }

        // find roots
        for(Level level : levelMap.values()) {
            if(level.isRoot()) {
                roots.add(level);
            }
        }

        // breadth first search to establish presentation order
        establishPresentationOrder(roots);
    }

    public List<Level> getRoots() {
        return roots;
    }


    /**
     * @return  the Level associated with the given {@code formClassId}
     */
    public Level getLevel(ResourceId formClassId) {
        return levelMap.get(formClassId);
    }

    private void establishPresentationOrder(List<Level> parents) {
        for(Level child : parents) {
            this.levels.add(child);
            establishPresentationOrder(child.children);
        }
    }

    /**
     *
     * @return a list of levels in this tree, in topologically sorted order
     */
    public List<Level> getLevels() {
        return levels;
    }

    public boolean hasLevel(ResourceId classId) {
        return levelMap.containsKey(classId);
    }
}
