package com.fittshop.config;

import com.fittshop.domain.enums.RoleType;
import com.fittshop.domain.model.Customer;
import com.fittshop.domain.model.LoyaltyAccount;
import com.fittshop.domain.model.Product;
import com.fittshop.repository.CustomerRepository;
import com.fittshop.repository.LoyaltyAccountRepository;
import com.fittshop.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final LoyaltyAccountRepository loyaltyAccountRepository;

    public DataInitializer(ProductRepository productRepository,
                           CustomerRepository customerRepository,
                           LoyaltyAccountRepository loyaltyAccountRepository) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.loyaltyAccountRepository = loyaltyAccountRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            seedProducts();
        }
        if (customerRepository.count() == 0) {
            seedUsers();
        }
    }

    private void seedProducts() {
        List<Product> products = List.of(
                new Product(
                        "SUP-WHEY-100",
                        "100% Whey Protein Concentrado 900g - Baunilha Nobre",
                        "Proteína de altíssimo valor biológico, 24g de proteína por dose, 5.5g de BCAAs e matéria-prima importada 100% filtrada.",
                        new BigDecimal("139.90"),
                        "Suplementos",
                        "https://images.unsplash.com/photo-1579722821273-0f6c7d44362f?auto=format&fit=crop&w=600&q=80",
                        "Dose 30g: 24g Proteína, 2.1g Carbo, 1.5g Gorduras, 5.4g BCAA, 4.3g Glutamina.",
                        "HIPERTROFIA",
                        50,
                        5,
                        "LOTE-WH-2026A",
                        LocalDate.now().plusMonths(18),
                        0.9
                ),
                new Product(
                        "SUP-CREA-PURE",
                        "Creatina Creapure® 300g 100% Pura Micronizada",
                        "Selo de pureza Creapure® alemã. Aumento explosivo de força muscular, potência e hidratação celular intra-miofibrilar.",
                        new BigDecimal("119.90"),
                        "Suplementos",
                        "https://images.unsplash.com/photo-1593095948071-474c5cc2989d?auto=format&fit=crop&w=600&q=80",
                        "Dose 3g: 100% Creatina Monohidratada Creapure® pura. 0 calorias, 0 sódio.",
                        "HIPERTROFIA",
                        80,
                        10,
                        "LOTE-CR-2026B",
                        LocalDate.now().plusMonths(24),
                        0.3
                ),
                new Product(
                        "SUP-PRE-VENOM",
                        "Pré-Treino Venom Extreme 300g - Maçã Verde",
                        "Fórmula insana com 400mg de Cafeína Anidra, 3000mg de Beta-Alanina, 1000mg de Taurina e Tirosina para foco cirúrgico.",
                        new BigDecimal("149.90"),
                        "Suplementos",
                        "https://images.unsplash.com/photo-1546483875-ad9014c88eba?auto=format&fit=crop&w=600&q=80",
                        "Dose 10g: 400mg Cafeína, 3000mg Beta-Alanina, 1000mg Arginina, 1000mg Taurina.",
                        "FORCA_POWERLIFTING",
                        35,
                        5,
                        "LOTE-VN-2026C",
                        LocalDate.now().plusMonths(14),
                        0.35
                ),
                new Product(
                        "SUP-ISO-ZERO",
                        "Whey Isolate Zero Carbo 900g - Chocolate Belga",
                        "Proteína isolada com microfiltração por fluxo cruzado (CFM). Zero lactose, zero glúten e absorção ultrarrápida.",
                        new BigDecimal("219.90"),
                        "Suplementos",
                        "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?auto=format&fit=crop&w=600&q=80",
                        "Dose 30g: 27g Proteína Isolada, 0g Carbo, 0g Gordura, 6.2g BCAA.",
                        "DEFINICAO",
                        40,
                        5,
                        "LOTE-IZ-2026D",
                        LocalDate.now().plusMonths(16),
                        0.9
                ),
                new Product(
                        "ACE-BELT-10MM",
                        "Cinturão de Couro Legítimo 10mm Powerlifting",
                        "Fabricado em couro bovino selecionado com fivela dupla em aço inox. Estabilidade lombar e pressão intra-abdominal máxima.",
                        new BigDecimal("189.90"),
                        "Acessórios",
                        "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?auto=format&fit=crop&w=600&q=80",
                        "Couro 100% legítimo com 10mm de espessura. Certificado para treinos pesados de agachamento e levantamento terra.",
                        "FORCA_POWERLIFTING",
                        25,
                        3,
                        "LOTE-EQ-01",
                        LocalDate.now().plusYears(10),
                        1.2
                ),
                new Product(
                        "ROU-CAM-OVER",
                        "Camiseta Oversized FittShop Heavy Duty - Preta",
                        "Modelagem oversized streetwear/gym, 100% algodão peruano fio 30.1 com costura reforçada ombro a ombro.",
                        new BigDecimal("89.90"),
                        "Roupas",
                        "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=600&q=80",
                        "Algodão 100% penteado peruano 220g/m². Caimento folgado ideal para treino de costas e peito.",
                        "HIPERTROFIA",
                        60,
                        10,
                        "LOTE-RP-02",
                        LocalDate.now().plusYears(5),
                        0.25
                ),
                new Product(
                        "ACE-SHAKER-INOX",
                        "Coqueteleira Shaker Térmica Inox 750ml com Blender",
                        "Mantém sua bebida gelada por até 24 horas. Fecho antivazamento de clique duplo e escala medidora interna.",
                        new BigDecimal("69.90"),
                        "Acessórios",
                        "https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=600&q=80",
                        "Aço Inoxidável 304 livre de BPA e toxinas. Isolamento a vácuo com parede dupla.",
                        "RESISTENCIA_CARDIO",
                        100,
                        15,
                        "LOTE-EQ-03",
                        LocalDate.now().plusYears(10),
                        0.4
                ),
                new Product(
                        "COMBO-HIPER-PRO",
                        "Combo Hipertrofia Máxima (Whey 100% + Creatina Creapure + Shaker)",
                        "O kit definitivo para explosão muscular e recomposição corporal com desconto promocional integrado de 15%.",
                        new BigDecimal("279.90"),
                        "Combos",
                        "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?auto=format&fit=crop&w=600&q=80",
                        "Contém: 1x Whey 900g + 1x Creatina 300g Creapure® + 1x Shaker Inox Térmico 750ml.",
                        "HIPERTROFIA",
                        30,
                        5,
                        "LOTE-CB-01",
                        LocalDate.now().plusMonths(18),
                        1.6
                )
        );

        productRepository.saveAll(products);
    }

    private void seedUsers() {
        Customer atleta = new Customer("Lucas Atleta", "atleta@fittshop.com.br", "{noop}fitt123", "111.222.333-44", "(11) 98888-1111", RoleType.ROLE_CUSTOMER);
        Customer vendedora = new Customer("Camila Consultora", "vendedora@fittshop.com.br", "{noop}fitt123", "222.333.444-55", "(11) 98888-2222", RoleType.ROLE_SELLER);
        Customer estoquista = new Customer("Carlos Estoquista", "estoquista@fittshop.com.br", "{noop}fitt123", "333.444.555-66", "(11) 98888-3333", RoleType.ROLE_STOCKIST);
        Customer caixa = new Customer("Mariana Caixa", "caixa@fittshop.com.br", "{noop}fitt123", "444.555.666-77", "(11) 98888-4444", RoleType.ROLE_CASHIER);
        Customer gerente = new Customer("Roberto Gerente", "gerente@fittshop.com.br", "{noop}fitt123", "555.666.777-88", "(11) 98888-5555", RoleType.ROLE_MANAGER);
        Customer admin = new Customer("Admin FittShop", "admin@fittshop.com.br", "{noop}fitt123", "000.000.000-00", "(11) 98888-0000", RoleType.ROLE_ADMIN);

        List<Customer> saved = customerRepository.saveAll(List.of(atleta, vendedora, estoquista, caixa, gerente, admin));

        // Inicializa conta de fidelidade com 120 FittCoins de bônus de boas-vindas para o cliente
        loyaltyAccountRepository.save(new LoyaltyAccount(saved.get(0).getId(), 120, "BRONZE"));
    }
}
