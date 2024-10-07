pub mod tyme;

use std::ffi::c_int;
use tyme::get_today_solar;
use tyme4rs::tyme::solar::SolarDay;

#[no_mangle]
pub extern "C" fn add(left: c_int, right: c_int) -> c_int {
    return add_impl(left, right);
}

#[no_mangle]
pub extern "C" fn get_solar_day() -> SolarDay {
    return get_today_solar();
}

pub fn add_impl(left: c_int, right: c_int) -> c_int {
    left + right
}
