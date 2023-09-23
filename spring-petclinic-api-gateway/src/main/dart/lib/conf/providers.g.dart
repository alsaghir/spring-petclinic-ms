// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'providers.dart';

// **************************************************************************
// RiverpodGenerator
// **************************************************************************

String _$visitsHash() => r'b9263228d8c33d12aac3e1c4cfe85be7fb9b911a';

/// Copied from Dart SDK
class _SystemHash {
  _SystemHash._();

  static int combine(int hash, int value) {
    // ignore: parameter_assignments
    hash = 0x1fffffff & (hash + value);
    // ignore: parameter_assignments
    hash = 0x1fffffff & (hash + ((0x0007ffff & hash) << 10));
    return hash ^ (hash >> 6);
  }

  static int finish(int hash) {
    // ignore: parameter_assignments
    hash = 0x1fffffff & (hash + ((0x03ffffff & hash) << 3));
    // ignore: parameter_assignments
    hash = hash ^ (hash >> 11);
    return 0x1fffffff & (hash + ((0x00003fff & hash) << 15));
  }
}

/// See also [visits].
@ProviderFor(visits)
const visitsProvider = VisitsFamily();

/// See also [visits].
class VisitsFamily extends Family<AsyncValue<List<Visit>>> {
  /// See also [visits].
  const VisitsFamily();

  /// See also [visits].
  VisitsProvider call(
    dynamic ownerId,
  ) {
    return VisitsProvider(
      ownerId,
    );
  }

  @override
  VisitsProvider getProviderOverride(
    covariant VisitsProvider provider,
  ) {
    return call(
      provider.ownerId,
    );
  }

  static const Iterable<ProviderOrFamily>? _dependencies = null;

  @override
  Iterable<ProviderOrFamily>? get dependencies => _dependencies;

  static const Iterable<ProviderOrFamily>? _allTransitiveDependencies = null;

  @override
  Iterable<ProviderOrFamily>? get allTransitiveDependencies =>
      _allTransitiveDependencies;

  @override
  String? get name => r'visitsProvider';
}

/// See also [visits].
class VisitsProvider extends AutoDisposeFutureProvider<List<Visit>> {
  /// See also [visits].
  VisitsProvider(
    dynamic ownerId,
  ) : this._internal(
          (ref) => visits(
            ref as VisitsRef,
            ownerId,
          ),
          from: visitsProvider,
          name: r'visitsProvider',
          debugGetCreateSourceHash:
              const bool.fromEnvironment('dart.vm.product')
                  ? null
                  : _$visitsHash,
          dependencies: VisitsFamily._dependencies,
          allTransitiveDependencies: VisitsFamily._allTransitiveDependencies,
          ownerId: ownerId,
        );

  VisitsProvider._internal(
    super._createNotifier, {
    required super.name,
    required super.dependencies,
    required super.allTransitiveDependencies,
    required super.debugGetCreateSourceHash,
    required super.from,
    required this.ownerId,
  }) : super.internal();

  final dynamic ownerId;

  @override
  Override overrideWith(
    FutureOr<List<Visit>> Function(VisitsRef provider) create,
  ) {
    return ProviderOverride(
      origin: this,
      override: VisitsProvider._internal(
        (ref) => create(ref as VisitsRef),
        from: from,
        name: null,
        dependencies: null,
        allTransitiveDependencies: null,
        debugGetCreateSourceHash: null,
        ownerId: ownerId,
      ),
    );
  }

  @override
  AutoDisposeFutureProviderElement<List<Visit>> createElement() {
    return _VisitsProviderElement(this);
  }

  @override
  bool operator ==(Object other) {
    return other is VisitsProvider && other.ownerId == ownerId;
  }

  @override
  int get hashCode {
    var hash = _SystemHash.combine(0, runtimeType.hashCode);
    hash = _SystemHash.combine(hash, ownerId.hashCode);

    return _SystemHash.finish(hash);
  }
}

mixin VisitsRef on AutoDisposeFutureProviderRef<List<Visit>> {
  /// The parameter `ownerId` of this provider.
  dynamic get ownerId;
}

class _VisitsProviderElement
    extends AutoDisposeFutureProviderElement<List<Visit>> with VisitsRef {
  _VisitsProviderElement(super.provider);

  @override
  dynamic get ownerId => (origin as VisitsProvider).ownerId;
}
// ignore_for_file: type=lint
// ignore_for_file: subtype_of_sealed_class, invalid_use_of_internal_member, invalid_use_of_visible_for_testing_member
