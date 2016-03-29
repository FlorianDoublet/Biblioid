package flq.projectbooks.data.libraries;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import flq.projectbooks.data.Category;
import flq.projectbooks.database.CategoriesDataSource;
import flq.projectbooks.database.MySQLiteHelper;

/**
 * Created by flori on 08/02/2016.
 */
public class CategoryLibrary {

    private static CategoryLibrary categories;
    private static Context context;
    List<Category> categoryList;
    private CategoriesDataSource datasource;

    public CategoryLibrary() {
        categoryList = new ArrayList<>();
        datasource = new CategoriesDataSource(context);
        datasource.open();
        categoryList = datasource.getAllCategories();
        datasource.close();
    }

    public CategoryLibrary(MySQLiteHelper dbHelper) {
        categoryList = new ArrayList<>();
        datasource = new CategoriesDataSource(dbHelper);
        datasource.open();
        categoryList = datasource.getAllCategories();
        datasource.close();
    }

    public CategoryLibrary(Context _context) {
        context = _context;
        categories = new CategoryLibrary();
    }

    public static CategoryLibrary getInstance() {
        return categories;
    }

    public static CategoryLibrary getInstanceOrInitialize(Context _context) {
        if (categories == null) {
            new CategoryLibrary(_context);
        }
        return categories;
    }

    public Category Add(Category category) {
        categoryList.add(category);
        datasource.open();
        Category new_categoriy = datasource.createCategory(category.getName()); //Add book to database
        categoryList = datasource.getAllCategories(); //Update categories
        datasource.close();
        return new_categoriy;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public Category getNewCategory() {
        return new Category();
    }

    public void deleteCategory(Category category) {
        categoryList.remove(category);
    }

    public Category getCategoryById(long id) {
        for (int j = 0; j < categoryList.size(); j++) {
            if (categoryList.get(j).getId() == id) {
                return categoryList.get(j);
            }
        }
        return null;
    }

    //get an category with his name
    public Category getCategoryByName(String name) {
        for (Category category : categoryList) {
            if (category.getName().equals(name)) {
                return category;
            }
        }
        return null;
    }

    public void deleteCategoryById(int id) {
        for (int j = 0; j < categoryList.size(); j++) {
            if (categoryList.get(j).getId() == id) {
                //Remove from database
                Category temp = categoryList.get(j);
                datasource.open();
                datasource.deleteCategory(temp);
                datasource.close();

                //Remove from local list
                categoryList.remove(j);

                return;
            }
        }
    }

    public void updateLocalList() {
        datasource.open();
        categoryList = datasource.getAllCategories();
        datasource.close();
    }


    public void updateOrAddCategory(Category category) {
        long id = category.getId();
        if (id != -1) {
            for (int j = 0; j < categoryList.size(); j++) {
                if (categoryList.get(j).getId() == id) {
                    datasource.open();
                    datasource.updateCategory(category); //Update database
                    datasource.close();
                    categoryList.set(j, category); //Update local list
                    return;
                }
            }
        } else {
            datasource.open();
            datasource.createCategory(category.getName()); //Add category to database
            categoryList = datasource.getAllCategories(); //Update categories
            datasource.close();
        }
    }
}
