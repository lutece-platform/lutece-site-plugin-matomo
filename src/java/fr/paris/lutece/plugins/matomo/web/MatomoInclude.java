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
 *  3. Neither the description of 'Mairie de Paris' nor 'Lutece' nor the descriptions of its
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

import fr.paris.lutece.portal.service.content.PageData;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.includes.PageInclude;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Include of the Matomo analytics code
 */
public class MatomoInclude implements PageInclude
{
    private static final String PROPERTY_DEFAULT_SITE_ID = "matomo.default.site.id";
    private static final String PROPERTY_DEFAULT_SERVER_HTTP_URL = "matomo.default.server.http.url";
    private static final String PROPERTY_DEFAULT_SERVER_HTTPS_URL = "matomo.default.server.https.url";
    private static final String PROPERTY_DEFAULT_AUTH_TOKEN = "matomo.default.widget.auth.token";
    private static final String DSKEY_SITE_ID = "matomo.site_property.site.id";
    private static final String DSKEY_SERVER_HTTP_URL = "matomo.site_property.server.http.url";
    private static final String DSKEY_SERVER_HTTPS_URL = "matomo.site_property.server.https.url";
    private static final String DSKEY_AUTH_TOKEN = "matomo.site_property.widget.auth.token";
    private static final String MARK_MATOMO = "matomo";
    private static final String PLUGIN_NAME = "matomo";
    private static final String TEMPLATE_MATOMO_INCLUDE = "skin/plugins/matomo/matomo_analytics.html";
    private static final String MARK_SITE_ID = "site_id";
    private static final String MARK_SERVER_HTTP_URL = "server_http_url";
    private static final String MARK_SERVER_HTTPS_URL = "server_https_url";

    private final String _strDefaultSiteId;
    private final String _strDefaultServerHttpUrl;
    private final String _strDefaultServerHttpsUrl;
    private final String _strDefaultAuthToken;

    /**
     * Constructor
     */
    public MatomoInclude( )
    {
        _strDefaultSiteId = AppPropertiesService.getProperty( PROPERTY_DEFAULT_SITE_ID, "<no site id provided>" );
        _strDefaultServerHttpUrl = AppPropertiesService.getProperty( PROPERTY_DEFAULT_SERVER_HTTP_URL, "<no server http url provided>" );
        _strDefaultServerHttpsUrl = AppPropertiesService.getProperty( PROPERTY_DEFAULT_SERVER_HTTPS_URL, "<no server https url provided>" );
        _strDefaultAuthToken = AppPropertiesService.getProperty( PROPERTY_DEFAULT_AUTH_TOKEN, "" );

        // initialize the datastore if it has not yet been done
        if ( !DatastoreService.existsKey( DSKEY_SITE_ID ) )
        {
            DatastoreService.setDataValue( DSKEY_SITE_ID, _strDefaultSiteId );
        }
        if ( !DatastoreService.existsKey( DSKEY_SERVER_HTTP_URL ) )
        {
            DatastoreService.setDataValue( DSKEY_SERVER_HTTP_URL, _strDefaultServerHttpUrl );
        }
        if ( !DatastoreService.existsKey( DSKEY_SERVER_HTTPS_URL ) )
        {
            DatastoreService.setDataValue( DSKEY_SERVER_HTTPS_URL, _strDefaultServerHttpsUrl );
        }
        if ( !DatastoreService.existsKey( DSKEY_AUTH_TOKEN ) )
        {
            DatastoreService.setDataValue( DSKEY_AUTH_TOKEN, _strDefaultAuthToken );
        }
    }

    /**
     * Substitute specific Freemarker markers in the page template.
     * 
     * @param rootModel
     *            the HashMap containing markers to substitute
     * @param data
     *            A PageData object containing applications data
     * @param nMode
     *            The current mode
     * @param request
     *            The HTTP request
     */
    @Override
    public void fillTemplate( Map<String, Object> rootModel, PageData data, int nMode, HttpServletRequest request )
    {
        if ( PluginService.isPluginEnable( PLUGIN_NAME ) && ( request != null ) )
        {
            Map<String, Object> model = new HashMap<String, Object>( );
            String strSiteId = DatastoreService.getDataValue( DSKEY_SITE_ID, _strDefaultSiteId );
            String strServerHttpUrl = DatastoreService.getDataValue( DSKEY_SERVER_HTTP_URL, _strDefaultServerHttpUrl );
            String strServerHttpsUrl = DatastoreService.getDataValue( DSKEY_SERVER_HTTPS_URL, _strDefaultServerHttpsUrl );
            model.put( MARK_SITE_ID, strSiteId );
            model.put( MARK_SERVER_HTTP_URL, strServerHttpUrl );
            model.put( MARK_SERVER_HTTPS_URL, strServerHttpsUrl );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MATOMO_INCLUDE, request.getLocale( ), model );

            rootModel.put( MARK_MATOMO, template.getHtml( ) );
        }
        else
        {
            rootModel.put( MARK_MATOMO, "" );
        }
    }

}
