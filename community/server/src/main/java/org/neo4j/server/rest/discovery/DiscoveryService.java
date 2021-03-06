/**
 * Copyright (c) 2002-2014 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.server.rest.discovery;

import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.neo4j.kernel.configuration.Config;
import org.neo4j.server.configuration.ServerSettings;
import org.neo4j.server.rest.dbms.AuthenticationService;
import org.neo4j.server.rest.repr.AccessDeniedDiscoveryRepresentation;
import org.neo4j.server.rest.repr.DiscoveryRepresentation;
import org.neo4j.server.rest.repr.OutputFormat;
import org.neo4j.server.security.auth.SecurityCentral;
import org.neo4j.server.web.ServerInternalSettings;

import static org.neo4j.server.rest.dbms.AuthenticateHeaders.extractToken;

/**
 * Used to discover the rest of the server URIs through a HTTP GET request to
 * the server root (/).
 */
@Path( "/" )
public class DiscoveryService
{
    private final Config configuration;
    private final OutputFormat outputFormat;
    private final SecurityCentral security;

    public DiscoveryService( @Context Config configuration, @Context OutputFormat outputFormat,
                             @Context SecurityCentral security )
    {
        this.configuration = configuration;
        this.outputFormat = outputFormat;
        this.security = security;
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )
    public Response getDiscoveryDocument( @HeaderParam( "Authorization" ) String auth ) throws URISyntaxException
    {
        if( !configuration.get( ServerSettings.authorization_enabled )
            || security.userForToken(extractToken( auth )).privileges().APIAccess() )
        {
            String webAdminManagementUri = configuration.get( ServerInternalSettings.management_api_path ).getPath();
            String dataUri = configuration.get( ServerInternalSettings.rest_api_path ).getPath();

            return outputFormat.ok( new DiscoveryRepresentation( webAdminManagementUri, dataUri ) );
        }
        else
        {
            return outputFormat.ok( new AccessDeniedDiscoveryRepresentation( AuthenticationService.AUTHENTICATION_PATH ) );
        }
    }

    @GET
    @Produces( MediaType.WILDCARD )
    public Response redirectToBrowser()
    {
        return outputFormat.seeOther( configuration.get( ServerInternalSettings.browser_path ) );

    }
}
