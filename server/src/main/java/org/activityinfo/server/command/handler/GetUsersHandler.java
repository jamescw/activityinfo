package org.activityinfo.server.command.handler;

/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.extjs.gxt.ui.client.Style;
import com.google.inject.Inject;
import org.activityinfo.legacy.shared.command.GetUsers;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.command.result.UserResult;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.legacy.shared.exception.IllegalAccessCommandException;
import org.activityinfo.legacy.shared.model.PartnerDTO;
import org.activityinfo.legacy.shared.model.UserPermissionDTO;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.server.database.hibernate.entity.UserDatabase;
import org.activityinfo.server.database.hibernate.entity.UserPermission;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alex Bertram
 * @see org.activityinfo.legacy.shared.command.GetUsers
 */
public class GetUsersHandler implements CommandHandler<GetUsers> {

    private EntityManager em;

    @Inject
    public GetUsersHandler(EntityManager em) {
        this.em = em;
    }

    @Override
    public CommandResult execute(GetUsers cmd, User currentUser) throws CommandException {

        UserDatabase db = em.getReference(UserDatabase.class, cmd.getDatabaseId());

        UserPermission currentUserPermission = PermissionOracle.using(em).getPermissionByUser(db, currentUser);

        assertAuthorized(currentUserPermission);

        String whereClause = "up.database.id = :dbId and " +
                             "up.user.id <> :currentUserId and " +
                             "up.allowView = true";

        if (!currentUserPermission.isAllowManageAllUsers()) {
            whereClause += " and up.partner.id = " + currentUserPermission.getPartner().getId();
        }

        TypedQuery<UserPermission> query = em.createQuery("select up from UserPermission up where " +
                                                          whereClause + " " + composeOrderByClause(cmd),
                UserPermission.class)
                                             .setParameter("dbId", cmd.getDatabaseId())
                                             .setParameter("currentUserId", currentUser.getId());

        if (cmd.getOffset() > 0) {
            query.setFirstResult(cmd.getOffset());
        }
        if (cmd.getLimit() > 0) {
            query.setMaxResults(cmd.getLimit());
        }

        List<UserPermissionDTO> models = new ArrayList<>();
        for (UserPermission perm : query.getResultList()) {
            UserPermissionDTO dto = new UserPermissionDTO();
            dto.setEmail(perm.getUser().getEmail());
            dto.setName(perm.getUser().getName());
            dto.setOrganization(perm.getUser().getOrganization());
            dto.setJobtitle(perm.getUser().getJobtitle());
            dto.setAllowDesign(perm.isAllowDesign());
            dto.setAllowView(perm.isAllowView());
            dto.setAllowViewAll(perm.isAllowViewAll());
            dto.setAllowEdit(perm.isAllowEdit());
            dto.setAllowEditAll(perm.isAllowEditAll());
            dto.setAllowManageUsers(perm.isAllowManageUsers());
            dto.setAllowManageAllUsers(perm.isAllowManageAllUsers());
            dto.setPartner(new PartnerDTO(perm.getPartner().getId(), perm.getPartner().getName()));
            models.add(dto);
        }

        return new UserResult(models, cmd.getOffset(), queryTotalCount(cmd, currentUser, whereClause));
    }

    private void assertAuthorized(UserPermission currentUserPermission) {
        if (!currentUserPermission.isAllowManageUsers()) {
            throw new IllegalAccessCommandException(String.format(
                    "User %d does not have permission to view user permissions in database %d",
                    currentUserPermission.getUser().getId(),
                    currentUserPermission.getDatabase().getId()));
        }
    }

    private int queryTotalCount(GetUsers cmd, User currentUser, String whereClause) {
        return ((Number) em.createQuery("select count(up) from UserPermission up where " + whereClause)
                           .setParameter("dbId", cmd.getDatabaseId())
                           .setParameter("currentUserId", currentUser.getId())
                           .getSingleResult()).intValue();
    }

    private String composeOrderByClause(GetUsers cmd) {
        String orderByClause = " ";

        if (cmd.getSortInfo().getSortDir() != Style.SortDir.NONE) {
            String dir = cmd.getSortInfo().getSortDir() == Style.SortDir.ASC ? "asc" : "desc";
            String property = null;
            String field = cmd.getSortInfo().getSortField();

            if ("name".equals(field)) {
                property = "up.user.name";
            } else if ("email".equals(field)) {
                property = "up.user.email";
            } else if ("partner".equals(field)) {
                property = "up.partner.name";
            } else if (field != null && field.startsWith("allow")) {
                property = "up." + field;
            }

            if (property != null) {
                orderByClause = " order by " + property + " " + dir;
            }
        }
        return orderByClause;
    }
}
