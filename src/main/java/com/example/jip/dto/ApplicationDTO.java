    package com.example.jip.dto;

    import com.example.jip.entity.Application;
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
    public class ApplicationDTO {
         int id;
         String name;
         String category;
         String content;
         String img;
        Date created_date;
         Status status; // Đổi tên enum
         String reply;
         Date replied_date;
         TeacherDTO teacher;
         StudentDTO student;
        MultipartFile[] imgFile;

        public static enum Status {
            Pending, Approved, Rejected
        }

        // Cập nhật phương thức toDTOStatus để chuyển từ entity status sang DTO status
        public static ApplicationDTO.Status toDTOStatus(Application.Status entityStatus) { // Chú ý kiểu của entityStatus
            if (entityStatus == null) return null;
            switch (entityStatus) {
                case Pending:
                    return ApplicationDTO.Status.Pending;
                case Approved:
                    return ApplicationDTO.Status.Approved;
                case Rejected:
                    return ApplicationDTO.Status.Rejected;
                default:
                    throw new IllegalArgumentException("Unknown status: " + entityStatus);
            }
        }
        public void setImgFromList(List<String> fileUrls) {
            if (fileUrls != null && !fileUrls.isEmpty()) {
                // Chuyển đổi List<String> thành một chuỗi nối các URL (có thể sử dụng dấu phân cách như ", " hoặc "\n")
                this.img = String.join(", ", fileUrls);  // Hoặc có thể sử dụng "\n" để xuống dòng giữa các URL
            } else {
                this.img = "No files available";  // Nếu danh sách trống, gán chuỗi mặc định
            }
        }



    }
