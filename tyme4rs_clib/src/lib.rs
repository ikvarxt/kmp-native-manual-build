use std::ffi::c_int;

#[no_mangle]
pub extern "C" fn add(left: c_int, right: c_int) -> c_int {
    return add_impl(left, right);
}

pub fn add_impl(left: c_int, right: c_int) -> c_int {
    left + right
}
