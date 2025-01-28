package ar.edu.itba.paw.webapp.controller;

import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ar.edu.itba.paw.exceptions.IllegalPageException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.PageNotFoundException;
import ar.edu.itba.paw.exceptions.UserIsNotModException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Mod;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;
import ar.edu.itba.paw.services.ModderService;
import ar.edu.itba.paw.webapp.dto.CreateModderDTO;
import ar.edu.itba.paw.webapp.dto.ModderDTO;
import ar.edu.itba.paw.webapp.validators.interfaces.CommunityMustExistConstraint;
import ar.edu.itba.paw.webapp.validators.interfaces.UsernameMustExistConstraint;

@Path("/api/mods")
@Component
public class ModderController {
    @Autowired
    private ModderService modderService;

    @GET
    public Response listMods(@Context UriInfo uriInfo, @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("byCommunity") final String communityName,
            @QueryParam("byUsername") @DefaultValue("") final String username)
            throws NoSuchCommunityException, IllegalPageException, PageNotFoundException, UserNotFoundException {
        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setPageNumber(page);
        PaginatedDataWrapper<Mod> mods = modderService.findModsPaginated(username, communityName, paginationRequest);

        if (mods.getData().size() == 0) {
            return Response.noContent().build();
        }
        List<ModderDTO> postsDTOs = mods.getData().stream()
                .map(ModderDTO.mapper(uriInfo))
                .toList();

        return Response.ok(new GenericEntity<>(postsDTOs) {
        })
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", mods.getFirstPage()).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", mods.getTotalPages()).build(), "last")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", mods.getPreviousPage()).build(), "prev")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", mods.getNextPage()).build(), "next")
                .build();

    }

    @POST
    public Response addModder(@Context UriInfo uriInfo, @Valid @NotNull CreateModderDTO payload) {
        try {
            modderService.addModder(payload.getUsername(), payload.getCommunityId());
            return Response.created(uriInfo.getAbsolutePath()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Path("/{communityName}/{userId}")
    public Response removeModder(@Context UriInfo uriInfo,
            @PathParam("communityName") String communityName,
            @PathParam("userId") Long id)
            throws UserNotFoundException, NoSuchCommunityException, UserIsNotModException {
        modderService.removeModder(id, communityName);
        return Response.ok().build();
    }
}
