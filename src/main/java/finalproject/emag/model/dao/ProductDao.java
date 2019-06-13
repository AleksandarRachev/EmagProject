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
