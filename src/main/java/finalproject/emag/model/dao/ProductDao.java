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
