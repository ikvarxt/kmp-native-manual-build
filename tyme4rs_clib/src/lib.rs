#[no_mangle]
pub extern "C" fn add(left: u64, right: u64) -> u64 {
    return add_impl(left, right);
}

pub fn add_impl(left: u64, right: u64) -> u64 {
    left + right
}
