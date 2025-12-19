package org.yearup.data.mysql;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao {


    public MySqlCategoryDao(DataSource dataSource) {
        super(dataSource);

    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        // get all categories
        String sqlQuery = "SELECT * FROM categories";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sqlQuery); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Category category = mapRow(rs);
                // add category to list
                categories.add(category);
            }

        } catch (SQLException e) {
            System.out.println("Error with the database: " + e.getMessage());
        }
        return categories;
    }

    @Override
    public Category getById(int categoryId) {
        // get category by id

        String sqlQuery = "SELECT * FROM categories WHERE category_id = ?";


        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sqlQuery)) {

            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error with the database: " + e.getMessage());
        }
        return null;

    }

    @Override
    public Category create(Category category) {
        // create a new category
        String sqlQuery = "INSERT INTO categories (name, description) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sqlQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

                if (generatedKeys.next()) {

                    category.setCategoryId(generatedKeys.getInt(1));

                }
            }

        } catch (SQLException e) {
            System.out.println("Error with the database: " + e.getMessage());
        }


        return category;
    }

    @Override
    public void update(int categoryId, Category category) {
        // update category
        String sqlQuery = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sqlQuery); ResultSet rs = ps.executeQuery()) {

            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setInt(3, categoryId);

            ps.executeUpdate();


        } catch (SQLException e) {
            System.out.println("Error with the database: " + e.getMessage());
        }

    }

    @Override
    public void delete(int categoryId) {
        // delete category
        String sqlQuery = "DELETE FROM categories WHERE category_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlQuery);) {

            ps.setInt(1, categoryId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error with the database: " + e.getMessage());

        }
    }

    private Category mapRow(ResultSet row) throws SQLException {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category() {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
