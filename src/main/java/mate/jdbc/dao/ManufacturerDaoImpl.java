package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class ManufacturerDaoImpl implements ManufacturerDao {
    @Override
    public Manufacturer create(Manufacturer manufacturer) {
        String query = "INSERT INTO manufacturer(name, country) VALUES(?, ?);";

        try (Connection dbConnection = ConnectionUtil.getConnection();
             PreparedStatement addManufacturerStatement =
                     dbConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            addManufacturerStatement.setString(1, manufacturer.getName());
            addManufacturerStatement.setString(2, manufacturer.getCountry());
            addManufacturerStatement.executeUpdate();
            ResultSet generatedKeys = addManufacturerStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                manufacturer.setId(id);
            }
            return manufacturer;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert data to db. "
                    + "Manufacturer: " + manufacturer, e);
        }
    }

    @Override
    public Optional<Manufacturer> get(Long id) {
        String query = "SELECT * FROM manufacturer "
                     + "WHERE id = ? AND is_deleted = FALSE;";
        Manufacturer manufacturer = new Manufacturer();

        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getManufacturerStatement = connection.prepareStatement(query);) {
            getManufacturerStatement.setLong(1, id);
            ResultSet resultSet = getManufacturerStatement.executeQuery();

            if (resultSet.next()) {
                parseManufacturer(resultSet);
            }
            return Optional.of(manufacturer);
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get manufacturer from db. Id = " + id, e);
        }
    }

    @Override
    public List<Manufacturer> getAll() {
        String query = "SELECT * FROM manufacturer "
                     + "WHERE is_deleted = FALSE;";
        List<Manufacturer> manufacturers = new ArrayList<>();

        try (Connection dbConnection = ConnectionUtil.getConnection();
             Statement getAllManufacturersStatement = dbConnection.createStatement()) {
            ResultSet resultSet = getAllManufacturersStatement.executeQuery(query);

            while (resultSet.next()) {
                manufacturers.add(parseManufacturer(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all data from db", e);
        }
        return manufacturers;
    }

    @Override
    public Manufacturer update(Manufacturer manufacturer) {
        String query = "UPDATE manufacturer SET name = ?, country = ? "
                     + "WHERE id = ? AND is_deleted = FALSE;";

        try (Connection dbConnection = ConnectionUtil.getConnection();
             PreparedStatement updateManufacturerStatement =
                     dbConnection.prepareStatement(query);) {
            updateManufacturerStatement.setString(1, manufacturer.getName());
            updateManufacturerStatement.setString(2, manufacturer.getCountry());
            updateManufacturerStatement.setLong(3, manufacturer.getId());
            updateManufacturerStatement.executeUpdate();
            return manufacturer;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update manufacturer in db."
                    + " Manufacturer: " + manufacturer, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE manufacturer SET is_deleted = 1 WHERE id = ?";

        try (Connection dbConnection = ConnectionUtil.getConnection();
             PreparedStatement deleteManufacturerStatement =
                     dbConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            deleteManufacturerStatement.setLong(1, id);
            return deleteManufacturerStatement.executeUpdate() >= 1;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete manufacturer from db. Id = " + id, e);
        }
    }

    public Manufacturer parseManufacturer(ResultSet resultSet) {
        try {
            Long id = resultSet.getObject(1, Long.class);
            String name = resultSet.getString(2);
            String country = resultSet.getString(3);
            return new Manufacturer(id, name, country);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
