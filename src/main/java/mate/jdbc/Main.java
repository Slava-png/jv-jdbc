package mate.jdbc;

import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Manufacturer;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerDao manufacturerDao =
                (ManufacturerDao) injector.getInstance(ManufacturerDao.class);

        manufacturerDao.create(new Manufacturer("BMW", "Germany"));
        manufacturerDao.create(new Manufacturer("KIA", "South Korea"));
        manufacturerDao.create(new Manufacturer("Mercedes", "Japan"));
        manufacturerDao.create(new Manufacturer("Toyota", "Japan"));
        manufacturerDao.create(new Manufacturer("Citroen", "Italy"));
        manufacturerDao.create(new Manufacturer("Peugeot", "France"));
        manufacturerDao.create(new Manufacturer("Aston Martin", "England"));

        manufacturerDao.update(new Manufacturer(5L, "Opel", "USA"));

        manufacturerDao.get(4L);

        manufacturerDao.delete(5L);

        manufacturerDao.getAll().forEach(System.out::println);
    }
}
