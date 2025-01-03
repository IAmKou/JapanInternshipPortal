// Hàm thay đổi ngôn ngữ
function changeLanguage(language) {
    // Cập nhật nhãn hiển thị ngôn ngữ
    const languageLabel = document.getElementById('languageLabel');
    if (languageLabel) {
        languageLabel.innerText = language === 'en' ? 'EN' : 'JA';
    }

    // Lưu ngôn ngữ được chọn vào localStorage
    localStorage.setItem('language', language);

    if (language === 'en') {
        // Reload trang nếu là tiếng Anh
        window.location.reload();
        return;
    }

    // Nếu là tiếng Nhật, gửi yêu cầu dịch
    fetch(`/api/translate/whole-page`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            Accept: 'application/json',
        },
        body: JSON.stringify({
            text: document.body.innerHTML, // Nội dung trang
            targetLanguage: language      // Ngôn ngữ đích
        })
    })
        .then(response => response.json())
        .then(data => {
            if (data.translatedText) {
                // Thay đổi nội dung trang sau khi dịch
                document.body.innerHTML = data.translatedText;

                if (languageLabel) {
                    languageLabel.innerText = 'JA';
                }
            }
        })
        .catch(error => {
            console.error('Lỗi dịch trang:', error);
            alert('Có lỗi xảy ra khi dịch trang. Vui lòng thử lại sau.');
        });
}

// Tự động kiểm tra ngôn ngữ và áp dụng
document.addEventListener('DOMContentLoaded', function () {
    const language = localStorage.getItem('language') || 'en';

    if (language !== 'en') {
        changeLanguage(language);
    }
});
