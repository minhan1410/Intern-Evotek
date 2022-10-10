# Ứng dụng xem, nhận thông báo về ngày ra mắt xe

## DB:

- User: id, username, password, email, role, isEnable
- Car: id, name, engine, img, countView, status, productLaunchDate

## Mô tả:

- Chưa đăng nhập:
    - Đăng nhập
    - Đăng kí xác thực qua email
    - Quên mk
    - Xem xe, tăng số lượt xem(tối đa 5 lần 1p)
- Đã đăng nhập:
    - Đổi mk
    - Nhận thông báo về ngày ra mắt xe qua email
    - User:
        - Xem xe, tăng số lượt xem(tối đa 5 lần 1p)
    - Admin:
        - Xem xe
        - Thêm, sửa, xóa xe
        - Upload ảnh qua cloudinary