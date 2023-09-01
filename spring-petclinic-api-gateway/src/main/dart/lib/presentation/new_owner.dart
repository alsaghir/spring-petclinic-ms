import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';

import '../conf/providers.dart';
import '../domain/owner.dart';
import '../domain/owner_repo.dart';

/// https://docs.flutter.dev/cookbook/forms
class NewOwnerScreen extends HookConsumerWidget {
  NewOwnerScreen({super.key});

  final _formKey = GlobalKey<FormState>();

  final _firstNameController = TextEditingController();
  final _lastNameController = TextEditingController();
  final _addressController = TextEditingController();
  final _cityController = TextEditingController();
  final _telephoneController = TextEditingController();

  final StateProvider<bool> loadingProvider =
  StateProvider<bool>((ref) => false);

  @override
  Widget build(BuildContext context, WidgetRef ref) {

    final dio = ref.watch(httpClientProvider);
    final isLoading = ref.watch(loadingProvider);

    return Scaffold(
      appBar: AppBar(
        centerTitle: false,
        automaticallyImplyLeading: false,
        title: const Text('Create new user'),
        leading: IconButton(
          onPressed: () async {
            await ref.read(ownersNotifierProvider.notifier).fetchAllOwners();
            if(!context.mounted) return;
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
                            ref.read(loadingProvider.notifier).state =
                            true;
                            var owner = Owner(
                                id: null,
                                firstName: _firstNameController.text,
                                lastName: _lastNameController.text,
                                address: _addressController.text,
                                city: _cityController.text,
                                telephone: _telephoneController.text);
                            Response response;
                            try {
                              response = await dio.post(
                                  "http://localhost:7778/api/customer/owners",
                                  data: owner.toJson(),
                                  options: Options(
                                      contentType:
                                      Headers.jsonContentType));


                              if (response.statusCode == 201) {
                                await ref.read(ownersNotifierProvider.notifier).fetchAllOwners();
                                if (!context.mounted) return;
                                print("about to pop");
                                GoRouter.of(context).pop();
                              }
                            } catch (err) {
                              print(err);
                            } finally {

                              ref.read(loadingProvider.notifier).state = false;
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
