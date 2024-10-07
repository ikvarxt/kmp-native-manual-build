use tyme4rs::tyme::solar::SolarDay;

pub fn get_today_solar() -> SolarDay {
    let solar = SolarDay::from_ymd(2000, 5, 7);
    return solar;
}

#[test]
pub fn solar_name() {
    let solar: SolarDay = SolarDay::from_ymd(1999, 2, 5);
    println!("{}", solar.to_string());
    assert_eq!(1999, solar.get_year())
}
