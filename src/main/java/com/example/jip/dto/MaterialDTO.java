    package com.example.jip.dto;

    import lombok.AccessLevel;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import lombok.experimental.FieldDefaults;
    import org.springframework.web.multipart.MultipartFile;

    import java.util.Date;
    import java.util.List;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public class MaterialDTO {
        private int id;
        private String title;
        private String content;
        private String img;
        private Date created_date;
        private TeacherDTO teacher;
        MultipartFile[] imgFile;
        private Integer teacherId; // Thêm trường này

        public void setImgFromList(List<String> fileUrls) {
            if (fileUrls != null && !fileUrls.isEmpty()) {
                // Chuyển đổi List<String> thành một chuỗi nối các URL (có thể sử dụng dấu phân cách như ", " hoặc "\n")
                this.img = String.join(", ", fileUrls);  // Hoặc có thể sử dụng "\n" để xuống dòng giữa các URL
            } else {
                this.img = "No files available";  // Nếu danh sách trống, gán chuỗi mặc định
            }
        }


    }
