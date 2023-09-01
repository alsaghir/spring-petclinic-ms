class Constants {
  static final kTabs = [
    TabData('Home', 'home'),
    TabData('Owners', 'owners'),
    TabData('Veterinarians', 'veterinarians'),
    TabData('Error', 'error')
  ];
}

class TabData {
  final String tabName;
  final String routeName;

  TabData(this.tabName, this.routeName);
}
