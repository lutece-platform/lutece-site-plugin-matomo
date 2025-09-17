/*
 * Copyright (c) 2002-2019, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.matomo.web;

import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.cdi.mvc.Models;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

/**
 * ManageMatomo JSP Bean class for JSP Bean
 */
@RequestScoped
@Named
@Controller( controllerJsp = "Matomo.jsp", controllerPath = "jsp/admin/plugins/matomo/", right = "MATOMO_MANAGEMENT" )
public class ManageMatomoJspBean extends MVCAdminJspBean
{
    public static final String RIGHT_MATOMO_MANAGEMENT = "MATOMO_MANAGEMENT";
    private static final long serialVersionUID = 1L;

    // templates
    private static final String TEMPLATE_MATOMO_DASHBOARD = "/admin/plugins/matomo/matomo.html";
    private static final String PROPERTY_PAGE_TITLE_MATOMO_DASHBOARD = "matomo.dashboard.pageTitle";
    private static final String DSKEY_AUTH_TOKEN = "matomo.site_property.widget.auth.token";
    private static final String VIEW_MATOMO_HOME = "home";
    private static final String MARK_AUTH_TOKEN = "auth_token";

    @Inject
    private Models model;

    /**
     * Return the MatomoDashboard page
     * 
     * @param request
     *            The HTTP request
     * @return The model
     */
    @View( value = VIEW_MATOMO_HOME, defaultView = true )
    public String getMatomoDashboard( HttpServletRequest request )
    {
        String strAuthToken = DatastoreService.getDataValue( DSKEY_AUTH_TOKEN, "" );

        if ( !strAuthToken.trim( ).equals( "" ) )
        {
            model.put( MARK_AUTH_TOKEN, strAuthToken );
        }

        return getPage( PROPERTY_PAGE_TITLE_MATOMO_DASHBOARD, TEMPLATE_MATOMO_DASHBOARD, model );
    }
}
