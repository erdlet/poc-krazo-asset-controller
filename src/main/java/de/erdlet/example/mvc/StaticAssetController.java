package de.erdlet.example.mvc;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

@Path("static")
@RequestScoped
public class StaticAssetController {

    private static final String BASE_PATH_TEMPLATE = "/WEB-INF/static/%s/";

    private interface SupportedPaths {

        String CSS = "css";
        String IMAGES = "images";
        String JAVASCRIPT = "js";
        String FONTS = "fonts";
    }

    @Inject
    HttpServletRequest request;

    @Inject
    ServletContext servletContext;

    @GET
    @Path("{type}/{fileName}")
    public Response getCss(@PathParam("type") final String type,
                           @PathParam("fileName") final String fileName) {
        try {
            final var file = resolveAsset(resolveBasePath(type), fileName);

            if (file == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

            return Response.ok(file)
                    .type(servletContext.getMimeType(fileName))
                    .header(HttpHeaders.CONTENT_DISPOSITION, fileName)
                    .header(HttpHeaders.CONTENT_LENGTH, file.length())
                    .header(HttpHeaders.LAST_MODIFIED, file.lastModified())
                    .build();

        } catch (final InvalidAssetPathException ex) {
            return Response.status(Status.NOT_FOUND).build();
        } catch (final Exception ex) {
            return Response.serverError().build();
        }
    }

    private String resolveBasePath(final String type) throws InvalidAssetPathException {
        return switch (type) {
            case SupportedPaths.CSS, SupportedPaths.JAVASCRIPT, SupportedPaths.IMAGES, SupportedPaths.FONTS ->
                    String.format(BASE_PATH_TEMPLATE, type);
            default -> throw new InvalidAssetPathException();
        };
    }

    private File resolveAsset(final String basePath, final String fileName)
            throws MalformedURLException, URISyntaxException {
        final var url = request.getServletContext().getResource(basePath + fileName);

        return url == null ? null : new File(url.toURI());
    }
}
