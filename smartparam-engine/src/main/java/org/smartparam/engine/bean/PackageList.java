package org.smartparam.engine.bean;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Encapsulates list of packages to scan, can be iterated. Order is retained.
 *
 * @author Adam Dubiel
 * @since 0.1.0
 */
public class PackageList implements Iterable<String> {

    /**
     * Internal representation of package list.
     */
    private List<String> packages = null;

    /**
     * Created internal list if it does not exist.
     */
    private void createIfNull() {
        if (packages == null) {
            packages = new LinkedList<String>();
        }
    }

    /**
     * Add new packages to list.
     *
     * @param packageName package name
     */
    public void addPackage(String packageName) {
        createIfNull();
        packages.add(packageName);
    }

    /**
     * Return ordered list of all packages.
     *
     * @return package names
     */
    public List<String> getPackages() {
        createIfNull();
        return packages;
    }

    /**
     * Replaces currently defined packages with those on provided list.
     *
     * @param packages list of package, content is copied
     */
    public void setPackages(List<String> packages) {
        createIfNull();
        packages.clear();
        this.packages.addAll(packages);
    }

    @Override
    public Iterator<String> iterator() {
        createIfNull();
        return packages.iterator();
    }
}