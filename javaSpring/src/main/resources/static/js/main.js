function deleteJob(endpoint, id) {
    if (confirm("Bạn chắc chắn xóa không?") === true) { 
        fetch(endpoint + id, {
            method: "delete"
        }).then(res => {
            if (res.status === 204) {
                alert("Xóa thành công!");
                location.reload();
            } else 
                alert("Có lỗi xảy ra!");
        });
    }
}
function deleteUser(endpoint, id) {
    if (confirm("Bạn chắc chắn xóa không?") === true) {
        fetch(endpoint + id, {
            method: "delete"
        }).then(res => {
            if (res.status === 204) {
                alert("Xóa thành công!");
                location.reload();
            } else
                alert("Có lỗi xảy ra!");
        });
    }
}
