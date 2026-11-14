/*
 * Copyright (c) 2002-2026, City of Paris
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

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import freemarker.core.HTMLOutputFormat;
import freemarker.core.TemplateHTMLOutputModel;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Renders the tracking code that MatomoInclude puts in the model of the portal page (LUT-33153): the marker matomo is
 * markup, written as is by the cConsent macro of the core inside its tarteaucitron script, and the site id and the
 * server URLs read from the site properties are written in it as JavaScript. Renders also the back office dashboard,
 * whose widget URL is built from the same properties.
 */
public class MatomoIncludeRenderTest extends LuteceTestCase
{
    private static final String PLUGIN_NAME = "matomo";
    private static final String MARK_MATOMO = "matomo";
    private static final String DSKEY_SITE_ID = "matomo.site_property.site.id";
    private static final String DSKEY_SERVER_HTTP_URL = "matomo.site_property.server.http.url";
    private static final String DSKEY_SERVER_HTTPS_URL = "matomo.site_property.server.https.url";
    private static final String [ ] DSKEYS = {
            DSKEY_SITE_ID, DSKEY_SERVER_HTTP_URL, DSKEY_SERVER_HTTPS_URL
    };
    private static final String TEMPLATE_DASHBOARD = "/admin/plugins/matomo/matomo.html";

    /** The way cConsent.ftl writes the marker, inside the script of cConsentTac. */
    private static final String CONSENT_SCRIPT = "<script>${matomo?is_markup_output?then(matomo, matomo?no_esc)}</script>";

    private final Map<String, String> _mapSavedValues = new HashMap<>( );
    private boolean _bPluginStatus;

    @BeforeEach
    protected void saveSiteProperties( )
    {
        for ( String strKey : DSKEYS )
        {
            _mapSavedValues.put( strKey, DatastoreService.getDataValue( strKey, null ) );
        }
        Plugin plugin = PluginService.getPlugin( PLUGIN_NAME );
        _bPluginStatus = plugin.isInstalled( );
        plugin.setStatus( true );
    }

    @AfterEach
    protected void restoreSiteProperties( )
    {
        for ( Map.Entry<String, String> entry : _mapSavedValues.entrySet( ) )
        {
            if ( entry.getValue( ) == null )
            {
                DatastoreService.removeData( entry.getKey( ) );
            }
            else
            {
                DatastoreService.setDataValue( entry.getKey( ), entry.getValue( ) );
            }
        }
        PluginService.getPlugin( PLUGIN_NAME ).setStatus( _bPluginStatus );
    }

    /**
     * Fills the model of a portal page with the include and renders the marker as cConsent does.
     *
     * @param strSiteId
     *            the site id property
     * @param strServerUrl
     *            the http and https server URL properties
     * @return the script
     * @throws Exception
     *             if the rendering fails
     */
    private static String renderConsentScript( String strSiteId, String strServerUrl ) throws Exception
    {
        DatastoreService.setDataValue( DSKEY_SITE_ID, strSiteId );
        DatastoreService.setDataValue( DSKEY_SERVER_HTTP_URL, strServerUrl );
        DatastoreService.setDataValue( DSKEY_SERVER_HTTPS_URL, strServerUrl );

        Map<String, Object> model = new HashMap<>( );
        new MatomoInclude( ).fillTemplate( model, null, 0, new MockHttpServletRequest( ) );
        assertTrue( model.get( MARK_MATOMO ) instanceof TemplateHTMLOutputModel, String.valueOf( model.get( MARK_MATOMO ) ) );

        Configuration cfg = new Configuration( Configuration.VERSION_2_3_0 );
        cfg.setOutputFormat( HTMLOutputFormat.INSTANCE );
        cfg.setAutoEscapingPolicy( Configuration.ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY );
        StringWriter writer = new StringWriter( );
        new Template( "page", CONSENT_SCRIPT, cfg ).process( model, writer );

        return writer.toString( );
    }

    /**
     * A numeric site id is written as a number, the server URL as a JavaScript string, neither HTML-escaped.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testTrackingCodeIsWrittenAsJavaScript( ) throws Exception
    {
        String strScript = renderConsentScript( " 12 ", "https://stats.example.org/matomo/?a=1&b='c\"" );

        assertTrue( strScript.contains( "tarteaucitron.user.matomoId = 12;" ), strScript );
        assertTrue( strScript.contains( "\"https://stats.example.org/matomo/?a=1&b=\\'c\\\"\"" ), strScript );
        assertFalse( strScript.contains( "&amp;" ), strScript );
        assertFalse( strScript.contains( "&lt;" ), strScript );
        assertFalse( strScript.contains( "&quot;" ), strScript );
    }

    /**
     * A site id that is not a number, such as the default value of a site not configured, is written as a string.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testSiteIdNotANumberIsWrittenAsAString( ) throws Exception
    {
        String strScript = renderConsentScript( "<no site id provided>", "" );

        assertTrue( strScript.contains( "tarteaucitron.user.matomoId = '<no site id provided>';" ), strScript );
    }

    /**
     * Renders the back office dashboard.
     *
     * @param strSiteId
     *            the site id property
     * @param strServerUrl
     *            the https server URL property
     * @param strAuthToken
     *            the widget token, or null
     * @return the page
     */
    private static String renderDashboard( String strSiteId, String strServerUrl, String strAuthToken )
    {
        DatastoreService.setDataValue( DSKEY_SITE_ID, strSiteId );
        DatastoreService.setDataValue( DSKEY_SERVER_HTTPS_URL, strServerUrl );
        Map<String, Object> model = new HashMap<>( );
        if ( strAuthToken != null )
        {
            model.put( "auth_token", strAuthToken );
        }

        return AppTemplateService.getTemplate( TEMPLATE_DASHBOARD, Locale.FRENCH, model ).getHtml( );
    }

    /**
     * The widget URL writes the server URL and the site id as text and the token URL-encoded.
     */
    @Test
    public void testDashboardWritesTheWidgetUrlAsText( )
    {
        String strHtml = renderDashboard( "12\"&x=<b>", "https://stats.example.org/matomo/?a=\"b\"", "to ken&=\"" );

        assertTrue( strHtml.contains( "src=\"https://stats.example.org/matomo/?a=&quot;b&quot;index.php?" ), strHtml );
        assertTrue( strHtml.contains( "&idSite=12%22%26x%3D%3Cb%3E&period=" ), strHtml );
        assertTrue( strHtml.contains( "&amp;token_auth=to%20ken%26%3D%22\"" ), strHtml );
        assertFalse( strHtml.contains( "<b>" ), strHtml );
    }

    /**
     * A server URL whose scheme is not http or https is not written: the page says that the dashboard is not
     * configured.
     */
    @Test
    public void testDashboardRefusesAServerUrlThatIsNotHttp( )
    {
        String strHtml = renderDashboard( "12", "javascript:void(0)/", null );

        assertFalse( strHtml.contains( "<iframe" ), strHtml );
        assertFalse( strHtml.contains( "javascript:" ), strHtml );
    }
}
