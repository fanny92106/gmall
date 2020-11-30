package service;

import bean.BaseCatalog1;
import bean.BaseCatalog2;
import bean.BaseCatalog3;

import java.util.List;

public interface ManageService {

    // search first catalog
    public List<BaseCatalog1> getCatalog1();

    // search second catalog based on first
    public List<BaseCatalog2> getCatalog2(String catalog1Id);

    // search third catalog based on second
    public List<BaseCatalog3> getCatalog3(String catalog2Id);
}
