//    private double getMaxPriceOfProduct() throws SQLException {
//        try (Connection c = jdbcTemplate.getDataSource().getConnection();) {
//            PreparedStatement ps = c.prepareStatement("SELECT MAX(price) FROM emag.products;");
//            ResultSet rs = ps.executeQuery();
//            rs.next();
//            return rs.getDouble(1);
//        }
//    }
//
//    private double getMaxPriceOfProductForSubcategory(long subcatId) throws SQLException {
//        try (Connection c = jdbcTemplate.getDataSource().getConnection();) {
//            String sql = "SELECT MAX(price) FROM emag.products WHERE subcategory_id = ?;";
//            PreparedStatement ps = c.prepareStatement(sql);
//            ps.setLong(1, subcatId);
//            ResultSet rs = ps.executeQuery();
//            rs.next();
//            return rs.getDouble(1);
//        }
//    }
//
//    private double getMinPriceOfProduct() throws SQLException {
//        try (Connection c = jdbcTemplate.getDataSource().getConnection();) {
//            String sql = "SELECT MIN(price) FROM emag.products;";
//            PreparedStatement ps = c.prepareStatement(sql);
//            ResultSet rs = ps.executeQuery();
//            rs.next();
//            return rs.getDouble(1);
//        }
//    }
//
//    private double getMinPriceOfProductForSubcategory(long subcatId) throws SQLException {
//        try (Connection c = jdbcTemplate.getDataSource().getConnection();) {
//            String sql = "SELECT MIN(price) FROM emag.products WHERE subcategory_id = ?;";
//            PreparedStatement ps = c.prepareStatement(sql);
//            ps.setLong(1, subcatId);
//            ResultSet rs = ps.executeQuery();
//            rs.next();
//            return rs.getDouble(1);
//        }
//    }
//
//    private int getReviewsCountForProduct(long productId) throws SQLException{
//        try(Connection c = jdbcTemplate.getDataSource().getConnection();) {
//            String sql = "SELECT COUNT(*) FROM reviews WHERE product_id = ?";
//            PreparedStatement ps = c.prepareStatement(sql);
//            ps.setLong(1, productId);
//            ResultSet rs = ps.executeQuery();
//            rs.next();
//            return rs.getInt(1);
//        }
//    }
//
//    private int getReviewsAvgGradeForProduct(long productId) throws SQLException{
//        try(Connection c = jdbcTemplate.getDataSource().getConnection();) {
//            String sql = "SELECT ROUND(AVG(grade)) FROM reviews WHERE product_id = ?";
//            PreparedStatement ps = c.prepareStatement(sql);
//            ps.setLong(1, productId);
//            ResultSet rs = ps.executeQuery();
//            rs.next();
//            return rs.getInt(1);
//        }
//    }
//
//    public ArrayList<GlobalViewProductDto> searchProducts(String name) throws Exception {
//        try (Connection c = jdbcTemplate.getDataSource().getConnection();) {
//            String sql = "SELECT id, product_name, price, quantity FROM products " +
//                    "WHERE product_name LIKE '%" + name + "%';";
//            PreparedStatement ps = c.prepareStatement(sql);
//            ResultSet rs = ps.executeQuery();
//            ArrayList<GlobalViewProductDto> products = products(rs);
//            if (products.size() == 0) {
//                throw new WrongSearchWordException();
//            }
//            return products;
//        }
//    }
//
//    private void addReviewsToProduct(Product p, long id) throws SQLException{
//        try(Connection c = jdbcTemplate.getDataSource().getConnection();) {
//            String sql = "SELECT user_id, title, comment, grade FROM reviews WHERE product_id = ?";
//            PreparedStatement ps = c.prepareStatement(sql);
//            ps.setLong(1, id);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                Review review = new Review();
////                review.setUserId(rs.getLong(1));
////                review.setProductId(id);
//                review.setTitle(rs.getString(2));
//                review.setComment(rs.getString(3));
//                review.setGrade(rs.getInt(4));
////                p.addToReviews(review);
//            }
//        }
//    }
//
//
//    public CartProductDto getProductForCart(long productId) throws Exception{
//        checkIfProductExists(productId);
//        checkProductQuantity(productId, 1);
//        try(Connection c = jdbcTemplate.getDataSource().getConnection();) {
//            String sql = "SELECT product_name, price, quantity FROM products WHERE id=?";
//            PreparedStatement ps = c.prepareStatement(sql);
//            ps.setLong(1, productId);
//            ResultSet rs = ps.executeQuery();
//            CartProductDto p = new CartProductDto();
//            while (rs.next()) {
//                p.setId(productId);
//                p.setName(rs.getString(1));
//                p.setPrice(rs.getDouble(2));
//            }
//            return p;
//        }
//    }
//
//    private void checkProductQuantity(long id, int products) throws Exception {
//        try (Connection c = jdbcTemplate.getDataSource().getConnection();) {
//            String sql = "SELECT quantity, product_name FROM products WHERE id = ?";
//            PreparedStatement ps = c.prepareStatement(sql);
//            ps.setLong(1, id);
//            ResultSet rs = ps.executeQuery();
//            rs.next();
//            int quantity = rs.getInt(1);
//            String name = rs.getString(2);
//            if (quantity >= products) {
//                return;
//            }
//            throw new ProductOutOfStockException("The product " + name + " is out of stock right now.");
//        }
//    }
//
//    public ArrayList<CartViewProductDto> viewCart(HashMap<CartProductDto, Integer> userCart) {
//        HashMap<CartProductDto, Integer> products = userCart;
//        ArrayList<CartViewProductDto> cart = new ArrayList<>();
//        for (Map.Entry<CartProductDto, Integer> e : products.entrySet()) {
//            CartViewProductDto p = new CartViewProductDto();
//            p.setId(e.getKey().getId());
//            p.setName(e.getKey().getName());
//            p.setQuantity(e.getValue());
//            p.setPrice(e.getKey().getPrice() * e.getValue());
//            cart.add(p);
//        }
//        return cart;
//    }
//
//    public void makeOrder(User user, HashMap<CartProductDto, Integer> userCart) throws Exception{
//        HashMap<CartProductDto, Integer> products = userCart;
//        for (Map.Entry<CartProductDto, Integer> e : products.entrySet()) {
//            checkProductQuantity(e.getKey().getId(), e.getValue());
//        }
//        double price = 0;
//        for (Map.Entry<CartProductDto, Integer> e : products.entrySet() ){
//            price += (e.getKey().getPrice() * e.getValue());
//        }
//        Connection c = jdbcTemplate.getDataSource().getConnection();
//        try {
//            c.setAutoCommit(false);
//            long productId = insertOrder(user, c, price);
//            insertOrderProducts(c, products, productId);
//            updateQuantity(c, products);
//            c.commit();
//        }
//        catch (SQLException e) {
//            c.rollback();
//            throw new SQLException();
//        }
//        finally {
//            c.setAutoCommit(true);
//            c.close();
//        }
//    }
//
//    private long insertOrder(User u, Connection c, double price) throws SQLException{
//        String sql = "INSERT INTO orders (user_id, total_price, order_date) VALUES (?, ?, ?)";
//        PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
//        ps.setLong(1, u.getId());
//        ps.setDouble(2, price);
//        java.sql.Date date = Date.valueOf(LocalDate.now().toString());
//        ps.setDate(3, date);
//        ps.execute();
//        ResultSet rs = ps.getGeneratedKeys();
//        rs.next();
//        return rs.getLong(1);
//    }
//
//    private void insertOrderProducts(Connection c, HashMap<CartProductDto, Integer> products, long orderId)
//            throws SQLException {
//        for (Map.Entry<CartProductDto, Integer> e : products.entrySet()) {
//            String sql = "INSERT INTO ordered_products (order_id, product_id, quantity) VALUES (?, ?, ?)";
//            PreparedStatement ps = c.prepareStatement(sql);
//            ps.setLong(1, orderId);
//            ps.setLong(2, e.getKey().getId());
//            ps.setInt(3, e.getValue());
//            ps.execute();
//            ps.close();
//        }
//    }
//
//    private void updateQuantity(Connection c, HashMap<CartProductDto, Integer> products) throws SQLException {
//        for (Map.Entry<CartProductDto, Integer> e : products.entrySet()) {
//            String sql = "UPDATE products SET quantity = quantity - ? WHERE id = ? ";
//            PreparedStatement ps = c.prepareStatement(sql);
//            ps.setInt(1, e.getValue());
//            ps.setLong(2, e.getKey().getId());
//            ps.execute();
//            ps.close();
//        }
//    }
//
//    private void notifyForPromotion(String title,String message) throws SQLException, MessagingException {
//        ArrayList<NotifyUserDto> users = new ArrayList<>();
//        try(Connection connection = this.jdbcTemplate.getDataSource().getConnection()) {
//            String sql = "SELECT email,full_name FROM users WHERE subscribed = ?";
//            PreparedStatement ps = connection.prepareStatement(sql);
//            ps.setBoolean(1,true);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()){
//                users.add(new NotifyUserDto(rs.getString(1),rs.getString(2)));
//            }
//        }
//        new Thread(new Runnable() {
//            @Override
//            public void run(){
//                for (NotifyUserDto user : users){
//                    try {
//                        MailUtil.sendMail("testingemag19@gmail.com",user.getEmail(),title,message);
//                    } catch (MessagingException e) {
//                        System.out.println("Ops there was a problem sending the email.");
//                    }
//                }
//            }
//        }).start();
//    }
