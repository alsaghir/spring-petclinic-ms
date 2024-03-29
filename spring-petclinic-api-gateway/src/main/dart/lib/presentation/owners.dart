import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:url_launcher/link.dart';

import '../domain/owner_repo.dart';
import 'constants.dart';

class OwnersScreen extends HookConsumerWidget {
  const OwnersScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final routes = Constants.kTabs;
    return Scaffold(
      appBar: AppBar(
        title: const Center(
          child: Text('Petclinic'),
        ),
        actions: List<Widget>.generate(
          routes.length,
              (index) => Link(
            uri: Uri.parse(routes[index].routeName),
            builder: (context, followLink) => ElevatedButton(
              autofocus: GoRouterState.of(context).uri.toString().substring(1) ==
                  routes[index].routeName
                  ? true
                  : false,
              onPressed: () =>
                  GoRouter.of(context).goNamed(routes[index].routeName),
              child: Text(routes[index].tabName),
            ),
          ),
        ),
      ),
      body: SafeArea(
        child: Column(
          children: [
            SearchOwnersWidget(),
            const OwnersListWidget(),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => GoRouter.of(context).goNamed("newOwner"),
        tooltip: 'add owner',
        child: const Icon(Icons.add),
      ),
    );
  }
}

class OwnersListWidget extends HookConsumerWidget {
  const OwnersListWidget({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final owners = ref.watch(ownersNotifierProvider);

    return Expanded(
      child: ListView.separated(
          separatorBuilder: (context, index) => const SizedBox(
                height: 5,
              ),
          padding: const EdgeInsets.all(8),
          itemCount: owners.length,
          itemBuilder: (context, index) {
            return ListTile(
              // dense: true,
              tileColor: Colors.amber.withOpacity(0.2),
              contentPadding:
                  const EdgeInsets.symmetric(vertical: 0, horizontal: 5),
              shape: RoundedRectangleBorder(
                side: BorderSide(width: 1, color: Colors.teal.withOpacity(0.7)),
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
                '${owners[index].firstName} ${owners[index].lastName}',
                textAlign: TextAlign.center,
                style:
                    const TextStyle(fontSize: 14, fontWeight: FontWeight.w800),
              ),
              subtitle: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      const Text('Address: '),
                      Text(
                        owners[index].address,
                        style: const TextStyle(fontWeight: FontWeight.w800),
                      ),
                    ],
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      const Text('City: '),
                      Text(
                        owners[index].city,
                        style: const TextStyle(fontWeight: FontWeight.w800),
                      ),
                    ],
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      const Text('Telephone: '),
                      Text(
                        owners[index].telephone,
                        style: const TextStyle(fontWeight: FontWeight.w800),
                      ),
                    ],
                  ),
                ],
              ),
              trailing: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  IconButton(
                      onPressed: () {
                        GoRouter.of(context).goNamed("editOwner",
                            pathParameters: {"id": "${owners[index].id}"});
                      },
                      icon: const Icon(
                        Icons.edit,
                        color: Colors.green,
                        size: 16,
                      )),
                  IconButton(
                      onPressed: () {
                        GoRouter.of(context).goNamed("viewOwner",
                            pathParameters: {"id": "${owners[index].id}"});
                      },
                      icon: const Icon(
                        Icons.preview,
                        color: Colors.green,
                        size: 16,
                      )),
                ],
              ),
            );
          }),
    );
  }
}

class SearchOwnersWidget extends HookConsumerWidget {
  SearchOwnersWidget({super.key});

  final TextEditingController _searchController = TextEditingController();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
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
            ref
                .watch(ownersNotifierProvider.notifier)
                .filter(_searchController.value.text);
          },
          decoration: InputDecoration(
            hintText: 'Search within first and last name then press Enter...',
            // Add a clear button to the search bar
            suffixIcon: IconButton(
              icon: const Icon(Icons.clear),
              onPressed: () => _searchController.clear(),
            ),
            // Add a search icon or button to the search bar
            prefixIcon: IconButton(
              icon: const Icon(Icons.search),
              onPressed: () {
                ref
                    .watch(ownersNotifierProvider.notifier)
                    .filter(_searchController.value.text);
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
