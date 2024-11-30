package com.example.librarymanagement.controller;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

final class ControllerUtils {

    /**
     * Build URI from current request and replace last path
     *
     * @param path path new last path
     * @return URI (as String) after replace last path
     */
    public static String URIRequestAndReplaceLastPath(String path) {
        var uri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri();
        String pathString = uri.getPath();
        int index = pathString.lastIndexOf("/");
        pathString = pathString.substring(0, index);
        pathString += path;
        return uri.resolve(pathString).toString();
    }

}
