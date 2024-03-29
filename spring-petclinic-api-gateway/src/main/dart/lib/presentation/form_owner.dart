import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';

import '../conf/providers.dart';
import '../domain/owner.dart';
import '../domain/owner_repo.dart';

final AutoDisposeFutureProviderFamily<Owner?, int?> ownerToEditProvider =
    FutureProvider.autoDispose.family<Owner?, int?>((ref, ownerId) {
  if (ownerId == null) return null;
  final httpClient = ref.watch(httpClientProvider);
  final config = ref.watch(asyncConfigProvider.future);
  return config.then((conf) async {
    Response<Map<String, dynamic>> ownerResponse = await httpClient.get(
        "${conf.ownersEndpoint()}/$ownerId",
        options: Options(contentType: Headers.jsonContentType));
    final owner = Owner.from(ownerResponse.data as Map<String, dynamic>);
    return owner;
  });
});

/// https://docs.flutter.dev/cookbook/forms
class NewOwnerScreen extends StatefulHookConsumerWidget {
  const NewOwnerScreen({required this.ownerId, super.key});

  final int? ownerId;

  @override
  NewOwnerScreenState createState() => NewOwnerScreenState();
}

class NewOwnerScreenState extends ConsumerState<NewOwnerScreen> {
  final _formKey = GlobalKey<FormState>();

  final _firstNameController = TextEditingController();
  final _lastNameController = TextEditingController();
  final _addressController = TextEditingController();
  final _cityController = TextEditingController();
  final _telephoneController = TextEditingController();

  final StateProvider<bool> loadingSubmitFormProvider =
      StateProvider<bool>((ref) => false);

  @override
  Widget build(BuildContext context) {
    final config = ref.watch(configProvider);
    final dio = ref.watch(httpClientProvider);
    final isLoading = ref.watch(loadingSubmitFormProvider);
    AsyncValue<Owner?> ownerForEdit =
        ref.watch(ownerToEditProvider(widget.ownerId));

    return ownerForEdit.when(
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (error, stackTrace) => const Text("Error while fetching owner"),
      data: (loadedOwner) {
        if (loadedOwner != null && _firstNameController.text.isEmpty) {
          _firstNameController.text = loadedOwner.firstName;
          _lastNameController.text = loadedOwner.lastName;
          _addressController.text = loadedOwner.address;
          _cityController.text = loadedOwner.city;
          _telephoneController.text = loadedOwner.telephone;
        }

        return Scaffold(
          appBar: AppBar(
            centerTitle: false,
            automaticallyImplyLeading: false,
            title: const Text('Create new user'),
            leading: IconButton(
              onPressed: () async {
                await ref
                    .read(ownersNotifierProvider.notifier)
                    .fetchAllOwners();
                if (!context.mounted) return;
                GoRouter.of(context).pop();
              },
              icon: const Icon(Icons.arrow_back),
            ),
          ),
          body: Align(
            alignment: Alignment.center,
            child: FractionallySizedBox(
              widthFactor: 0.5,
              child: ListView(
                // Prevent performance issues, use
                // Listview.builder() or CustomScrollView for optimization
                shrinkWrap: false,
                padding: const EdgeInsets.symmetric(
                    horizontal: 16.0, vertical: 2 * 16.0),
                physics: const BouncingScrollPhysics(),
                children: [
                  Form(
                    key: _formKey,
                    autovalidateMode: AutovalidateMode.onUserInteraction,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        NameListTile(
                          lastNameController: _firstNameController,
                          validator: (value) => value == null || value.isEmpty
                              ? "First name is required"
                              : null,
                          labelText: 'First Name',
                          hintText: 'Enter first name of the owner...',
                          icon: const Icon(Icons.person_rounded),
                          textInputType: TextInputType.name,
                        ),
                        const SizedBox(height: 16.0),
                        NameListTile(
                          lastNameController: _lastNameController,
                          validator: (value) => value == null || value.isEmpty
                              ? "Last name is required"
                              : null,
                          labelText: 'Last Name',
                          hintText: 'Enter last name of the owner...',
                          icon: const Icon(Icons.person_rounded),
                          textInputType: TextInputType.name,
                        ),
                        const SizedBox(height: 16.0),
                        NameListTile(
                          lastNameController: _addressController,
                          validator: (value) => value == null || value.isEmpty
                              ? "Address is required"
                              : null,
                          labelText: 'Address',
                          hintText: 'Enter address of the owner...',
                          icon: const Icon(Icons.location_on_rounded),
                          textInputType: TextInputType.name,
                        ),
                        const SizedBox(height: 16.0),
                        NameListTile(
                          lastNameController: _cityController,
                          validator: (value) => value == null || value.isEmpty
                              ? "City is required"
                              : null,
                          labelText: 'City',
                          hintText: 'Enter city of the owner...',
                          icon: const Icon(Icons.location_city_rounded),
                          textInputType: TextInputType.name,
                        ),
                        const SizedBox(height: 16.0),
                        NameListTile(
                          lastNameController: _telephoneController,
                          validator: (value) {
                            var reg = RegExp(r'[0-9]{12}');
                            if (value == null || value.isEmpty) {
                              return "Phone is required";
                            } else if (!reg.hasMatch(value)) {
                              return "Please enter a valid phone number";
                            } else {
                              return null;
                            }
                          },
                          labelText: 'Phone',
                          hintText: '905554443322',
                          icon: const Icon(Icons.phone_rounded),
                          textInputType: TextInputType.phone,
                        ),
                        const SizedBox(height: 2 * 16.0),
                        SizedBox(
                          width: double.infinity,
                          child: ElevatedButton.icon(
                            style: ElevatedButton.styleFrom(
                              backgroundColor: Theme.of(context).primaryColor,
                              foregroundColor: Colors.white,
                            ),
                            onPressed: isLoading
                                ? null
                                : () async {
                                    if (_formKey.currentState!.validate()) {
                                      ref
                                          .read(loadingSubmitFormProvider
                                              .notifier)
                                          .state = true;
                                      var owner = Owner(
                                          id: loadedOwner?.id,
                                          firstName: _firstNameController.text,
                                          lastName: _lastNameController.text,
                                          address: _addressController.text,
                                          city: _cityController.text,
                                          telephone: _telephoneController.text,
                                          pets: List.empty());
                                      Response response;
                                      try {
                                        if (loadedOwner == null) {
                                          response = await dio.post(
                                              config!.ownersEndpoint(),
                                              data: owner.toJson(),
                                              options: Options(
                                                  contentType:
                                                      Headers.jsonContentType));
                                        } else {
                                          response = await dio.put(
                                              "${config!.ownersEndpoint()}/${loadedOwner.id}",
                                              data: owner.toJson(),
                                              options: Options(
                                                  contentType:
                                                      Headers.jsonContentType));
                                        }
                                        if (response.statusCode == 201 ||
                                            response.statusCode == 204) {
                                          await ref
                                              .read(ownersNotifierProvider
                                                  .notifier)
                                              .fetchAllOwners();
                                          ref.invalidate(ownerProvider);
                                          if (!context.mounted) return;
                                          GoRouter.of(context).pop();
                                        }
                                      } catch (err) {
                                        print(err);
                                      } finally {
                                        ref
                                            .read(loadingSubmitFormProvider
                                                .notifier)
                                            .state = false;
                                      }
                                    }
                                  },
                            icon: !isLoading
                                ? const Icon(
                                    Icons.save_rounded,
                                    size: 20,
                                  )
                                : const SizedBox.shrink(),
                            label: !isLoading
                                ? const Text('Save')
                                : const SizedBox(
                                    height: 20,
                                    width: 20,
                                    child: CircularProgressIndicator(
                                      color: Colors.white,
                                      strokeWidth: 2,
                                    ),
                                  ),
                          ),
                        )
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ),
        );
      },
    );
  }
}

class NameListTile extends StatelessWidget {
  const NameListTile(
      {super.key,
      required TextEditingController lastNameController,
      required FormFieldValidator<String> validator,
      required String labelText,
      required String hintText,
      required Icon icon,
      required TextInputType textInputType})
      : _lastNameController = lastNameController,
        _validator = validator,
        _labelText = labelText,
        _hintText = hintText,
        _icon = icon,
        _textInputType = textInputType;

  final TextEditingController _lastNameController;
  final FormFieldValidator<String> _validator;
  final String _labelText;
  final String _hintText;
  final Icon _icon;
  final TextInputType _textInputType;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      leading: SizedBox(
        height: double.infinity,
        child: _icon,
      ),
      title: Row(
        mainAxisSize: MainAxisSize.max,
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Expanded(
            child: TextFormField(
              obscureText: false,
              controller: _lastNameController,
              style: const TextStyle(fontSize: 18),
              keyboardType: _textInputType,
              validator: _validator,
              decoration: InputDecoration(
                labelText: _labelText,
                hintText: _hintText,
                enabledBorder: OutlineInputBorder(
                  borderSide: BorderSide(
                    color: Theme.of(context).primaryColor,
                    width: 2,
                  ),
                  borderRadius: BorderRadius.circular(20),
                ),
                focusedBorder: OutlineInputBorder(
                  borderSide: BorderSide(
                    color: Theme.of(context).primaryColor,
                    width: 2,
                  ),
                  borderRadius: BorderRadius.circular(20),
                ),
                errorBorder: OutlineInputBorder(
                  borderSide: BorderSide(
                    color: Colors.red.withOpacity(0.5),
                    width: 2,
                  ),
                  borderRadius: BorderRadius.circular(20),
                ),
                focusedErrorBorder: OutlineInputBorder(
                  borderSide: BorderSide(
                    color: Colors.red.withOpacity(0.5),
                    width: 2,
                  ),
                  borderRadius: BorderRadius.circular(20),
                ),
                filled: true,
                contentPadding:
                    const EdgeInsetsDirectional.fromSTEB(16, 0, 0, 0),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
