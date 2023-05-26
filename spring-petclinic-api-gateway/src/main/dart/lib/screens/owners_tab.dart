import 'dart:collection';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:petclinic_ui/config.dart';

class OwnersTab extends StatefulWidget {
  const OwnersTab({Key? key}) : super(key: key);

  @override
  State<OwnersTab> createState() => _OwnersTabState();
}

class _OwnersTabState extends State<OwnersTab> {
  final TextEditingController _searchController = TextEditingController();
  final dio = Dio();

  @override
  void dispose() {
    super.dispose();
    _searchController.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Consumer<OwnerListState>(
      builder: (context, value, child) => Column(
        children: [
          SearchOwnersWidget(
              searchController: _searchController, ownerListState: value),
          OwnersListWidget(ownerListState: value),
        ],
      ),
    );
  }
}


class OwnersListWidget extends StatefulWidget {
  const OwnersListWidget({Key? key, required OwnerListState ownerListState})
      : _ownerListState = ownerListState,
        super(key: key);

  final OwnerListState _ownerListState;

  @override
  State<OwnersListWidget> createState() => _OwnersListWidgetState();
}

class _OwnersListWidgetState extends State<OwnersListWidget> {
  final dio = Dio();
  late OwnerListState _ownerListState;

  @override
  void initState() {
    super.initState();
    _ownerListState = widget._ownerListState;
    getOwners();
  }

  void getOwners() async {
    final Response<List<dynamic>> response =
        await dio.get(Config.ownersEndpoint());
    setState(() {
      widget._ownerListState.allOwners(Owner.fromJson(response));
    });
  }

  @override
  Widget build(BuildContext context) {
    return _ownerListState.owners.isEmpty
        ? const Center(child: Text('No record found'))
        : Expanded(
            child: ListView.separated(
                separatorBuilder: (context, index) => const SizedBox(
                      height: 5,
                    ),
                padding: const EdgeInsets.all(8),
                itemCount: _ownerListState.owners.length,
                itemBuilder: (context, index) {
                  return ListTile(
                    // dense: true,
                    tileColor: Colors.amber.withOpacity(0.2),
                    contentPadding:
                        const EdgeInsets.symmetric(vertical: 0, horizontal: 5),
                    shape: RoundedRectangleBorder(
                      side: BorderSide(
                          width: 1, color: Colors.teal.withOpacity(0.7)),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    leading: CircleAvatar(
                      backgroundColor: Colors.teal.withOpacity(0.5),
                      child: Text(
                        '${index + 1}',
                        style: const TextStyle(color: Colors.black),
                      ),
                    ),
                    title: Text(
                      '${_ownerListState.owners[index].firstName} ${_ownerListState.owners[index].firstName}',
                      textAlign: TextAlign.center,
                      style: const TextStyle(
                          fontSize: 14, fontWeight: FontWeight.w800),
                    ),
                    subtitle: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            const Text('Address: '),
                            Text(
                              _ownerListState.owners[index].address,
                              style:
                                  const TextStyle(fontWeight: FontWeight.w800),
                            ),
                          ],
                        ),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            const Text('City: '),
                            Text(
                              _ownerListState.owners[index].city,
                              style:
                                  const TextStyle(fontWeight: FontWeight.w800),
                            ),
                          ],
                        ),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            const Text('Telephone: '),
                            Text(
                              _ownerListState.owners[index].telephone,
                              style:
                                  const TextStyle(fontWeight: FontWeight.w800),
                            ),
                          ],
                        ),
                      ],
                    ),
                    trailing: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        IconButton(
                            onPressed: () {},
                            icon: const Icon(
                              Icons.edit,
                              color: Colors.green,
                              size: 16,
                            )),
                        IconButton(
                            onPressed: () {},
                            icon: const Icon(
                              Icons.delete,
                              color: Colors.red,
                              size: 16,
                            )),
                      ],
                    ),
                  );
                }),
          );
  }
}

class SearchOwnersWidget extends StatelessWidget {
  const SearchOwnersWidget({
    super.key,
    required TextEditingController searchController,
    required OwnerListState ownerListState,
  })  : _searchController = searchController,
        _ownerListState = ownerListState;

  final TextEditingController _searchController;
  final OwnerListState _ownerListState;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: Container(
        // Add padding around the search bar
        padding: const EdgeInsets.symmetric(horizontal: 8.0),
        // Use a Material design search bar
        child: TextFormField(
          textInputAction: TextInputAction.search,
          controller: _searchController,
          onFieldSubmitted: (value) {
            _ownerListState.filter(_searchController.value.text);
          },
          decoration: InputDecoration(
            hintText: 'Search within first and last name...',
            // Add a clear button to the search bar
            suffixIcon: IconButton(
              icon: Icon(Icons.clear),
              onPressed: () => _searchController.clear(),
            ),
            // Add a search icon or button to the search bar
            prefixIcon: IconButton(
              icon: Icon(Icons.search),
              onPressed: () {
                _ownerListState.filter(_searchController.value.text);
              },
            ),
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(20.0),
            ),
          ),
        ),
      ),
    );
  }
}

class Owner {
  int id;
  String firstName;
  String lastName;
  String address;
  String city;
  String telephone;

  Owner({
    required this.id,
    required this.firstName,
    required this.lastName,
    required this.address,
    required this.city,
    required this.telephone,
  });

  factory Owner.from(Map<String, dynamic> json) => Owner(
        id: json["id"],
        firstName: json["firstName"],
        lastName: json["lastName"],
        address: json["address"],
        city: json["city"],
        telephone: json["telephone"],
      );

  Map<String, dynamic> toJson() => {
        "id": id,
        "firstName": firstName,
        "lastName": lastName,
        "address": address,
        "city": city,
        "telephone": telephone,
      };

  static List<Owner> fromJson(Response<List<dynamic>> list) {
    if (list.data == null) {
      return List<Owner>.empty();
    }
    return (list.data as List<dynamic>).map((e) => Owner.from(e)).toList();
  }
}

class OwnerListState extends ChangeNotifier {
  List<Owner> _allOwners = List.empty();
  List<Owner> _currentOwners = List.empty();

  UnmodifiableListView<Owner> get owners =>
      UnmodifiableListView(_currentOwners);

  void filter(String nameSearchText) {
    List<String> names = nameSearchText.split(' ');
    _currentOwners = _allOwners
        .where((owner) =>
            nameSearchText.trim().isEmpty ||
            names.any((name) => owner.firstName.toLowerCase().contains(name.toLowerCase())) ||
            names.any((name) => owner.lastName.toLowerCase().contains(name.toLowerCase())))
        .toList();

    // This call tells the widgets that are listening to this model to rebuild.
    notifyListeners();
  }

  void allOwners(List<Owner> fromJson) {
    _allOwners = fromJson;
    _currentOwners = _allOwners;

    // This call tells the widgets that are listening to this model to rebuild.
    notifyListeners();
  }
}
