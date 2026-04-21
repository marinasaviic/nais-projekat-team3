package rs.ac.uns.acs.nais.GraphDatabaseService.service.impl;

import org.springframework.stereotype.Service;

import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.BaseFont;
import rs.ac.uns.acs.nais.GraphDatabaseService.model.Product;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.ProductRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.repository.CustomerRepository;
import rs.ac.uns.acs.nais.GraphDatabaseService.service.IProductService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.awt.*;

/**
 * Service layer for product management and graph-based recommendation in Neo4j.
 *
 * Provides CRUD operations on Product nodes (soft-delete via available=false),
 * delegates recommendation queries to the product repository, and generates
 * PDF product reports using OpenPDF.
 */
@Service
public class ProductService implements IProductService {

    public final ProductRepository productRepository;
    public final CustomerRepository customerRepository;

    public ProductService(ProductRepository productRepository, CustomerRepository customerRepository) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    /** Saves a new Product node and marks it as available. */
    @Override
    public Product addNewProduct(Product product) {
        product.setAvailable(true);
        return productRepository.save(product);
    }

    /**
     * Soft-deletes a product by setting available=false.
     * Returns false if the product with the given ID is not found.
     */
    @Override
    public boolean deleteProduct(String id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            product.get().setAvailable(false);
            productRepository.save(product.get());
            return true;
        }
        return false;
    }

    /**
     * Updates the product name.
     * Returns false if the product with the given ID is not found.
     */
    @Override
    public boolean updateProduct(String id, String productName) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            product.get().setName(productName);
            productRepository.save(product.get());
            return true;
        }
        return false;
    }

    @Override
    public List<Product> recommendProductsByPurchaseHistory(Long customerId) {
        return productRepository.recommendProductsByPurchaseHistory(customerId);
    }

    @Override
    public List<Product> recommendProductsByReviews(Long customerId) {
        return productRepository.recommendProductsByReviews(customerId);
    }

    /**
     * Generates a PDF report containing all given products in a three-column table
     * (name, availability, ID). Uses OpenPDF for document creation.
     *
     * @param products list of Product nodes to include in the report
     * @return PDF content as a byte array
     * @throws IOException if the PDF cannot be written
     */
    @Override
    public byte[] export(List<Product> products) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        com.lowagie.text.Document document = new com.lowagie.text.Document();

        PdfWriter.getInstance(document, byteArrayOutputStream);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, Font.BOLD);
        Paragraph title = new Paragraph("PRODUCT REPORT", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Three-column table: Product name | Availability | ID
        PdfPTable reportTable = new PdfPTable(3);
        reportTable.setWidthPercentage(100);

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.BOLD);
        PdfPCell headerCell1 = new PdfPCell(new Paragraph("Product", headerFont));
        PdfPCell headerCell2 = new PdfPCell(new Paragraph("Availability", headerFont));
        PdfPCell headerCell3 = new PdfPCell(new Paragraph("ID", headerFont));

        headerCell1.setBackgroundColor(new Color(110, 231, 234, 255));
        headerCell2.setBackgroundColor(new Color(110, 231, 234, 255));
        headerCell3.setBackgroundColor(new Color(110, 231, 234, 255));

        reportTable.addCell(headerCell1);
        reportTable.addCell(headerCell2);
        reportTable.addCell(headerCell3);

        for (Product product : products) {
            reportTable.addCell(product.getName());
            reportTable.addCell(String.valueOf(product.isAvailable()));
            reportTable.addCell(product.getId());
        }

        document.add(reportTable);
        document.close();

        return byteArrayOutputStream.toByteArray();
    }
}
